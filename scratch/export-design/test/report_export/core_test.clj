(ns report-export.core-test
  (:require
   [clojure.data.json :as json]
   [clojure.string :as str]
   [clojure.test :refer [deftest is are]]
   [report-export.core :as core]
   ;; loading this ns IS the extension: it only adds methods, core was untouched
   [report-export.extensions]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Fixtures
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def invoice
  {:report/kind     :invoice
   :report/id       "INV-042"
   :report/client   "Acme GmbH"
   :report/due-date "2026-10-31"
   :report/lines    [{:item "Consulting" :qty 3 :unit-price 1200M}
                     {:item "Travel"     :qty 1 :unit-price 350.50M}]})

(def statement
  {:report/kind            :statement
   :report/account         "CH93-0076"
   :report/period          "2026-Q3"
   :report/opening-balance 500M
   :report/transactions    [{:date "2026-07-01" :description "Salary" :amount 6500M}
                            {:date "2026-07-15" :description "Rent, July" :amount -2100M}]})

(def tax-form
  {:report/kind     :tax-form
   :report/taxpayer "Jane Doe"
   :report/year     2025
   :report/sections [{:section/name "Income"
                      :section/fields [{:field/label "Employment" :field/amount 82000M}
                                       {:field/label "Dividends"  :field/amount 1200M}]}
                     {:section/name "Deductions"
                      :section/fields [{:field/label "Pillar 3a" :field/amount 6883M}]}]})

(def expense-report
  {:report/kind     :expense-report
   :report/employee "jdoe"
   :report/quarter  "2026-Q3"
   :report/expenses [{:date "2026-08-03" :category "Travel"
                      :description "Zurich | conference" :amount 412.30}
                     {:date "2026-07-11" :category "Meals"
                      :description "Client dinner" :amount 88.00}]})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; PDF structural validation (header, xref offsets, stream length, trailer)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn assert-valid-pdf [bs]
  (let [s (String. ^bytes bs "ISO-8859-1")]
    (is (str/starts-with? s "%PDF-1.4"))
    (is (str/ends-with? (str/trimr s) "%%EOF"))
    (let [[_ start] (re-find #"\nstartxref\n(\d+)\n" s)]
      (is (.startsWith ^String s "xref" (Integer/parseInt start))
          "startxref points at the xref table"))
    (when-let [[_ entries] (re-find #"xref\n0 \d+\n0{10} 65535 f \n((?:\d{10} 00000 n \n)+)" s)]
      (doseq [[i off] (map-indexed vector (map parse-long (re-seq #"\d{10}" entries)))]
        (is (.startsWith ^String s (format "%d 0 obj" (inc i)) (int off))
            (format "xref offset for object %d is exact" (inc i)))))
    (when-let [[_ len body] (re-find #"(?s)<< /Length (\d+) >>\nstream\n(.*?)\nendstream" s)]
      (is (= (parse-long len) (count body)) "stream /Length is byte-exact"))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Base design: kinds
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(deftest invoice-derives-line-totals-and-meta
  (let [doc (core/document invoice)]
    (is (= "Invoice INV-042" (:doc/title doc)))
    (is (= ["Consulting" 3 1200M 3600M] (first (:doc/rows doc))))
    (is (= 3950.50M (get-in doc [:doc/meta :total])))
    (is (= "Acme GmbH" (get-in doc [:doc/meta :client])))))

(deftest statement-derives-running-balance
  (let [doc (core/document statement)]
    (is (= '(6500M 7000M) (take-last 2 (first (:doc/rows doc)))))
    (is (= 4900M (get-in doc [:doc/meta :closing-balance])))))

(deftest tax-form-flattens-sections-with-subtotals
  (let [doc (core/document tax-form)]
    (is (= ["Income" "Subtotal" 83200M] (nth (:doc/rows doc) 2)))
    (is (= 90083M (get-in doc [:doc/meta :grand-total])))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Base design: formats
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(deftest csv-exports
  (is (str/starts-with? (core/export invoice :csv) "item,qty,unit-price,line-total"))
  (is (str/starts-with? (core/export statement :csv) "date,description,amount,balance"))
  ;; comma inside a field is quoted, not mangled
  (is (some? (re-find #"\"Rent, July\"" (core/export statement :csv))))
  (is (str/ends-with? (core/export tax-form :csv)
                      "Deductions,Pillar 3a,6883\nDeductions,Subtotal,6883\n")))

(deftest json-exports-round-trip
  ;; JSON payload keys are the bare names (:key-fn name), read back as keywords
  (doseq [report [invoice statement tax-form]]
    (let [doc (core/document report)
          out (json/read-str (core/export report :json) :key-fn keyword)]
      (is (= (:doc/title doc) (:title out)))
      (is (= (:doc/columns doc) (:columns out)))
      (is (= (count (:doc/rows doc)) (count (:rows out)))))))

(deftest pdf-exports-are-structurally-valid
  (doseq [report [invoice statement tax-form]]
    (let [bs (core/export report :pdf)]
      (is (bytes? bs))
      (assert-valid-pdf bs)))
  (is (str/includes? (String. ^bytes (core/export invoice :pdf) "ISO-8859-1")
                     "(Invoice INV-042)"))
  ;; non-ASCII (em dash in the statement title) is sanitized, not dropped raw
  (is (str/includes? (String. ^bytes (core/export statement :pdf) "ISO-8859-1")
                     "Statement CH93-0076 ? 2026-Q3")))

(deftest export-to-file-and-extension-registry
  (let [f (java.io.File/createTempFile "invoice" (core/format->extension :pdf))]
    (try
      (core/export-to-file invoice :pdf f)
      (is (= (alength (core/export invoice :pdf)) (.length f)))
      (finally (.delete f))))
  (are [fmt ext] (= ext (core/format->extension fmt))
    :csv ".csv" :json ".json" :pdf ".pdf"))

(deftest unknown-format-fails-loudly
  (is (thrown? IllegalArgumentException (core/export invoice :xlsx))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Extension 1 (extensions.clj): new kind works with all untouched base formats
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(deftest new-kind-document-method
  (let [doc (core/document expense-report)]
    (is (= "Expenses jdoe 2026-Q3" (:doc/title doc)))
    (is (= ["2026-07-11" "Meals" "Client dinner" 88.0] (first (:doc/rows doc)))) ; sorted by date
    (is (= 500.3 (double (:total (:doc/meta doc)))))
    (is (= {"Meals" 88.0 "Travel" 412.3} (:by-category (:doc/meta doc))))))

(deftest new-kind-exports-to-all-base-formats
  (let [csv-out (core/export expense-report :csv)]
    (is (str/starts-with? csv-out "date,category,description,amount"))
    (is (< (str/index-of csv-out "Client dinner") (str/index-of csv-out "Zurich")))
    (is (str/includes? csv-out "Zurich | conference")))
  (let [j (json/read-str (core/export expense-report :json) :key-fn keyword)]
    (is (= 500.3 (double (get-in j [:meta :total])))))
  (assert-valid-pdf (core/export expense-report :pdf)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Extension 2 (extensions.clj): new format works for all untouched kinds
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(deftest new-format-renders-all-kinds
  (doseq [report [invoice statement tax-form expense-report]]
    (let [md (core/export report :markdown)]
      (is (str/starts-with? md "# "))
      (is (str/includes? md "| --- |"))
      (is (str/ends-with? md "\n"))))
  ;; pipe escaping on the expense fixture
  (is (str/includes? (core/export expense-report :markdown) "Zurich \\| conference"))
  ;; the new format registered its own file extension
  (is (= ".md" (core/format->extension :markdown))))
