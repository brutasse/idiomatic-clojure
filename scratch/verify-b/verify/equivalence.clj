(ns verify-b.equivalence
  "Loads the old and new report namespaces against stubbed app deps and
  compares them on a corpus of edge cases.")

;; --- stubs for the app namespaces the fixture requires --------------------
(intern (create-ns 'cheshire.core) 'write-str pr-str)
(def posts (atom []))
(def logs (atom []))
(intern (create-ns 'app.http) 'post!
        (fn [url opts] (swap! posts conj [url opts]) :ok))
(intern (create-ns 'app.log) 'info
        (fn [& args] (swap! logs conj args) nil))

(require '[report.old :as o] '[report.new :as n])

;; --- corpus ---------------------------------------------------------------
(def rows
  [{:id 1 :first "Ada" :last "Lovelace"
    :items [{:price 10 :qty 2} {:price 1.5 :qty 4}]}
   {:id 2 :first "Grace" :last "Hopper" :items []}
   {:id 3 :first "Alan" :last "Turing" :items [{:price 0 :qty 99}]}
   {:id 4 :first "B" :last "R" :items [{:price 7 :qty 1} {:price 3 :qty 1}]}
   {:id 5 :first "Dup" :last "N" :items [{:price 2 :qty 5}]}
   {:id 6 :first "Dup" :last "N" :items [{:price 1 :qty 1}]}])

(def reports (mapv o/build-report rows))

(def merges
  (for [r [{:id nil :name "a" :total 0}
           {:id 1 :name nil :total nil}
           {:id 0 :name nil :total 1}
           {:extra 1}
           {:id nil :name "a" :total 0 :extra 1}]
        :let [d {:id 9 :name "d" :total 9 :extra 2}]]
    [r d]))

(def corpora
  [['none []]
   ['all-zero (mapv #(hash-map :id % :total 0 :name "z") (range 3))]
   ['small reports]
   ['ties (mapv #(hash-map :id % :total (mod % 3) :name (str "n" (mod % 4))) (range 25))]
   ['wide (mapv #(hash-map :id % :total (* 10 (mod % 3)) :name (str "r" %)) (range 25))]])

(def results (atom {}))
(defn check [what a b]
  (swap! results assoc what (= a b)))

(defn logged
  "Returns [value log-calls] for (f coll) on a namespace's top-ten."
  [nsym coll]
  (reset! logs [])
  (let [v ((ns-resolve nsym 'top-ten) coll)]
    [v (doall @logs)]))

(defn check-effects []
  (doseq [[label coll] corpora]
    (let [[va la] (logged 'report.old coll)
          [vb lb] (logged 'report.new coll)]
      (check (str "top-ten/value-" label) va vb)
      (check (str "top-ten/logs-" label) la lb)))

  (reset! logs []) (o/log-matrix [1 2] [:a :b])
  (let [a (doall @logs)]
    (reset! logs []) (n/log-matrix [1 2] [:a :b])
    (check 'log-matrix a (doall @logs)))

  (reset! posts []) (o/notify-all! reports)
  (let [a (doall @posts)]
    (reset! posts []) (n/notify-all! reports)
    (check 'notify-all!/effects a (doall @posts))))

(defn run []
  (check 'build-report (mapv o/build-report rows) (mapv n/build-report rows))
  (check 'build-reports (doall (o/build-reports rows)) (doall (n/build-reports rows)))
  (check 'active-reports (doall (o/active-reports reports)) (doall (n/active-reports reports)))
  (check 'all-lines (doall (o/all-lines reports)) (doall (n/all-lines reports)))
  (check 'totals-by-name (o/totals-by-name reports) (n/totals-by-name reports))
  (check 'item-total (mapv o/item-total reports) (mapv n/item-total reports))
  (check 'merge-defaults (mapv #(apply o/merge-defaults %) merges)
         (mapv #(apply n/merge-defaults %) merges))
  (check-effects)
  (let [bad (into {} (filter (fn [[_ ok?]] (not ok?))) @results)]
    {:checked (count @results) :mismatches bad}))

(comment (run))
