(ns report-export.extensions
  "Proof of extensibility: everything in this file is ADDITIVE. Nothing in
  report-export.core was touched to make the new kind or new format work."
  (:require
   [clojure.string :as str]
   [report-export.core :as core]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; New report kind: expense reports.
;; One document method -> instantly exportable to PDF, CSV and JSON.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmethod core/document :expense-report
  [{:report/keys [employee quarter expenses] :as report}]
  (let [by-cat (group-by :category expenses)]
    (-> (core/report-document
         (-> report
             (dissoc :report/expenses)
             (assoc :report/title (str "Expenses " employee " " quarter)
                    :report/columns [:date :category :description :amount]
                    :report/rows (sort-by :date expenses))))
        (update :doc/meta
                assoc
                :total (transduce (map :amount) + 0M expenses)
                :by-category (into (sorted-map)
                                   (map (fn [[cat txs]]
                                          [cat (transduce (map :amount) + 0M txs)]))
                                   by-cat)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; New format: Markdown tables.
;; One render method -> instantly works for invoices, statements, tax forms
;; and expense reports alike, without touching any kind's code.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- md-cell [s]
  (-> (str s) (str/replace "|" "\\|") (str/replace "\n" " ")))

(defmethod core/render :markdown
  [{:doc/keys [title columns rows meta]} _format]
  (str
   (format "# %s\n\n" title)
   (str/join "\n" (for [[k v] meta] (format "- **%s:** %s" (name k) v)))
   "\n\n"
   (str "| " (str/join " | " columns) " |\n")
   (str "|" (str/join "|" (repeat (count columns) " --- ")) "|\n")
   (str/join "\n"
             (for [row rows]
               (str "| " (str/join " | " (map md-cell row)) " |")))
   "\n"))

(defmethod core/format->extension :markdown [_] ".md")
