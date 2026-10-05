(ns report.new
  (:require [app.http :as http]
            [app.log :as log]
            [cheshire.core :as json]))

(defn- line-total [{:keys [price qty]}]
  (* price qty))

(defn- sum-line-totals [items]
  (reduce + 0 (map line-total items)))

(defn- non-nil-or
  "V, or FALLBACK when V is nil. Unlike `or`, this keeps 0 and false."
  [v fallback]
  (if (nil? v) fallback v))

(defn build-report [row]
  {:id    (:id row)
   :name  (str (:first row) " " (:last row))
   :total (sum-line-totals (:items row))})

(defn build-reports [rows]
  (map build-report rows))

(defn active-reports [reports]
  (remove (comp zero? :total) reports))

(defn all-lines [reports]
  (mapcat :items reports))

(defn notify! [report]
  (http/post! "/notify" {:body (json/write-str report)}))

(defn notify-all! [reports]
  (run! notify! reports))

(defn totals-by-name [reports]
  (into {} (map (juxt :name :total)) reports))

(defn item-total [report]
  (sum-line-totals (:items report)))

(defn top-ten
  "Summary of the ten highest-total active reports, or nil when there are none."
  [reports]
  (let [top (->> reports active-reports (sort-by :total) reverse (take 10))]
    (when (seq top)
      (let [n (count top)]
        (log/info "top ten:" n)
        {:total   (transduce (map :total) + top)
         :by-name (totals-by-name top)
         :count   n}))))

(defn merge-defaults
  "Replace nil fields of `report` with those of `defaults`."
  [report defaults]
  {:id    (non-nil-or (:id report) (:id defaults))
   :name  (non-nil-or (:name report) (:name defaults))
   :total (non-nil-or (:total report) (:total defaults))})

(defn log-matrix [xs ys]
  (doseq [x xs y ys]
    (log/info x y)))
