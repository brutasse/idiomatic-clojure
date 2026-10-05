(ns report-export.core
  "Exporting reports to multiple formats, extensible on both axes.

  The two axes (report kinds x formats) are decoupled through a canonical
  document model:

      report data --(document multimethod, dispatch on :report/kind)--> document
      document    --(render multimethod,   dispatch on format)--------> String/bytes

  A new report kind provides ONE document method; all existing formats work
  for it for free. A new format provides ONE render method; all existing
  kinds work for it for free. Existing code is never edited."
  (:require
   [clojure.data.csv :as csv]
   [clojure.data.json :as json]
   [clojure.java.io :as io]
   [clojure.string :as str]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; The canonical document model
;;
;; A plain map, the lingua franca between the two axes:
;;   {:doc/title   string
;;    :doc/columns [string]
;;    :doc/rows    [[scalar]]            ;; same arity as :doc/columns
;;    :doc/meta    {keyword scalar}}     ;; header facts (client, dates, totals)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmulti document
  "Convert report data (a map tagged with :report/kind) into the canonical
  document model. This is the only thing a report kind must provide."
  :report/kind)

(defn- data->rows [columns rows]
  ;; rows are either seqs of maps (keyed by column) or seqs of seqs (positional)
  (mapv (fn [row]
          (if (map? row)
            (mapv #(get row % "") columns)
            (vec row)))
        rows))

(defn report-document
  "Assemble a canonical document from report data. :doc/meta picks up every
  :report/* key except the structural ones, so kinds only mention facts that
  belong in the export header. Kind methods call this; simple reports can
  skip defining a method entirely via the :default below."
  [{:report/keys [columns rows] :or {columns [] rows []} :as report}]
  {:doc/title   (:report/title report)
   :doc/columns (mapv name columns)
   :doc/rows    (data->rows columns rows)
   :doc/meta    (into (sorted-map)
                      (for [[k v] (dissoc report :report/kind :report/title
                                          :report/columns :report/rows)]
                        [(-> k name keyword) v]))})

(defmethod document :default [report]
  (report-document report))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Report kinds (axis 1)
;;
;; Each method normalizes a kind's irregular domain data into the flat
;; tabular model. Formats below never learn about any of this.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Invoices carry line items; the line totals and grand total are derived.
(defmethod document :invoice
  [{:report/keys [lines] :as invoice}]
  (let [line-total (fn [{:keys [qty unit-price]}] (* qty unit-price))]
    (-> (report-document
         (-> invoice
             (dissoc :report/lines)
             (assoc :report/title (str "Invoice " (:report/id invoice))
                    :report/columns [:item :qty :unit-price :line-total]
                    :report/rows (for [{:keys [item qty unit-price] :as l} lines]
                                   {:item item :qty qty :unit-price unit-price
                                    :line-total (line-total l)}))))
        (update :doc/meta assoc :total (transduce (map line-total) + 0M lines)))))

;; Bank statements carry transactions; a running balance column is derived.
(defmethod document :statement
  [{:report/keys [transactions opening-balance] :as stmt}]
  (let [balance (reductions + opening-balance (map :amount transactions))]
    (-> (report-document
         (-> stmt
             (dissoc :report/transactions)
             (assoc :report/title (str "Statement " (:report/account stmt) " — "
                                       (:report/period stmt))
                    :report/columns [:date :description :amount :balance]
                    :report/rows (map (fn [tx b] (assoc tx :balance b))
                                      transactions (rest balance)))))
        (update :doc/meta assoc :closing-balance (last balance)))))

;; Tax forms are irregular: sections of fields. Flattened into the tabular
;; model with a synthetic section column and one subtotal row per section.
(defmethod document :tax-form
  [{:report/keys [sections] :as form}]
  (-> (report-document
       (-> form
           (dissoc :report/sections)
           (assoc :report/columns [:section :field :amount]
                  :report/rows
                  (into []
                        (mapcat (fn [{:section/keys [name fields]}]
                                  (conj (mapv (fn [{:field/keys [label amount]}]
                                                {:section name :field label :amount amount})
                                              fields)
                                        {:section name :field "Subtotal"
                                         :amount (transduce (map :field/amount) + 0M fields)})))
                        sections))))
      (update :doc/meta
              assoc :grand-total
              (transduce (comp (mapcat :section/fields) (map :field/amount))
                         + 0M sections))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Formats (axis 2)
;;
;; render consumes a canonical document and returns the export: String for
;; text formats, byte[] for binary ones. It knows nothing about report kinds.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmulti render
  "Render a canonical document into the given format. This is the only thing
  a format must provide."
  (fn [_doc format] format))

(defmethod render :csv [{:doc/keys [columns rows]} _format]
  (with-out-str
    (csv/write-csv *out*
                   (cons columns (map (partial map str) rows)))))

(defmethod render :json [doc _format]
  ;; keys are exported without the :doc/ prefix: {"title" ... "meta" ...}
  (json/write-str doc :key-fn name))

;; Minimal, dependency-free PDF writer (single Courier page) producing real,
;; byte-accurate PDF 1.4 - binary output, hence byte[].
(defn- pdf-text [s]
  (-> (str s)
      (str/replace "\\" "\\\\")
      (str/replace "(" "\\(")
      (str/replace ")" "\\)")
      (str/replace #"[^\x20-\x7e]" "?")))

(defn- pdf-bytes [text-lines]
  (let [content (str "BT /F1 9 Tf 40 750 Td 12 TL\n"
                     (str/join "\n"
                               (map (fn [line] (format "(%s) Tj T*" (pdf-text line)))
                                    text-lines))
                     "\nET")
        objects ["<< /Type /Catalog /Pages 2 0 R >>"
                 "<< /Type /Pages /Kids [3 0 R] /Count 1 >>"
                 (str "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] "
                      "/Resources << /Font << /F1 5 0 R >> >> /Contents 4 0 R >>")
                 (format "<< /Length %d >>\nstream\n%s\nendstream"
                         (count content) content)
                 "<< /Type /Font /Subtype /Type1 /BaseFont /Courier >>"]
        header  "%PDF-1.4\n"
        bodies  (mapv (fn [i o] (format "%d 0 obj\n%s\nendobj\n" (inc i) o))
                      (range) objects)
        offsets (reductions + (count header) (map count bodies))]
    (.getBytes
     (str header
          (apply str bodies)
          (apply str (format "xref\n0 %d\n0000000000 65535 f \n" (inc (count objects)))
                 (map (fn [off] (format "%010d 00000 n \n" off))
                      (take (count objects) offsets)))
          (format "trailer\n<< /Size %d /Root 1 0 R >>\nstartxref\n%d\n%%%%EOF\n"
                  (inc (count objects))
                  (+ (count header) (reduce + (map count bodies)))))
     "ISO-8859-1")))

(defmethod render :pdf
  [{:doc/keys [title columns rows meta]} _format]
  (pdf-bytes
   (concat [title ""]
           (for [[k v] meta] (format "%s: %s" (name k) v))
           ["" (str/join " | " columns)]
           (map (fn [row] (str/join " | " (map str row))) rows))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Public API
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn export
  "Export report data (a map tagged with :report/kind) into the given format.
  Returns a String for text formats, byte[] for binary ones."
  [report format]
  (render (document report) format))

(defmulti format->extension
  "File suffix per format; formats register their own, so new formats stay
  self-contained."
  identity)
(defmethod format->extension :csv [_] ".csv")
(defmethod format->extension :json [_] ".json")
(defmethod format->extension :pdf [_] ".pdf")

(defn export-to-file
  "Write export of report to f. The caller picks the suffix, typically via
  (format->extension format)."
  [report format f]
  (io/make-parents f)
  (with-open [w (io/output-stream f)]
    (let [out (export report format)]
      (if (bytes? out)
        (.write ^java.io.OutputStream w ^bytes out)
        (.write ^java.io.Writer w ^String out))))
  f)
