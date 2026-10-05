(ns report.old
  (:require [app.http :as http]
            [app.log :as log]
            [cheshire.core :as json]))

(defn build-report [row]
  {:id    (:id row)
   :name  (str (:first row) " " (:last row))
   :total (apply + (map #(* (:price %) (:qty %)) (:items row)))})

(defn build-reports [rows]
  (map build-report rows))

(defn active-reports [reports]
  (filter #(not (zero? (:total %))) reports))

(defn all-lines [reports]
  (apply concat (map :items reports)))

(defn notify! [report]
  (http/post! "/notify" {:body (json/write-str report)}))

(defn notify-all! [reports]
  (doall (map notify! reports)))

(defn totals-by-name [reports]
  (into {} (map (fn [r] [(:name r) (:total r)]) reports)))

(defn item-total [report]
  (loop [items (:items report)
         acc   0]
    (if (empty? items)
      acc
      (recur (rest items)
             (+ acc (* (:price (first items)) (:qty (first items))))))))

(defn top-ten [reports]
  (let [reports (active-reports reports)
        reports (sort-by :total reports)
        reports (reverse reports)
        reports (take 10 reports)]
    (when (not (empty? reports))
      (let [total   (apply + (map :total reports))
            by-name (totals-by-name reports)]
        (do
          (log/info "top ten:" (count reports))
          {:total   total
           :by-name by-name
           :count   (count reports)})))))

(defn merge-defaults [report defaults]
  {:id    (if (nil? (:id report)) (:id defaults) (:id report))
   :name  (if (nil? (:name report)) (:name defaults) (:name report))
   :total (if (nil? (:total report)) (:total defaults) (:total report))})

(defn log-matrix [xs ys]
  (doseq [x xs]
    (doseq [y ys]
      (log/info x y))))
