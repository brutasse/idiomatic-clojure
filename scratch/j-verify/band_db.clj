(ns band.db)

(def bands
  [{:name "Kowelski" :genre :rock  :active? true}
   {:name "Silent"   :genre :rock  :active? false}
   {:name "Mash"     :genre :metal :active? true}])

(defn active?
  "Whether the band is still playing."
  [{:keys [active?]}]
  active?)

(defn display-name
  "Name with genre in brackets, e.g. \"Kowelski [rock]\"."
  [{:keys [name genre]}]
  (str name " [" genre "]"))

(defn loud-active
  "Upper-cased display names of the active bands."
  []
  (->> bands
       (filter active?)
       (map #(clojure.string/upper-case (display-name %)))))

(def odd-inc
  (comp (filter odd?) (map inc)))

(println (doall (loud-active)))
(println (transduce odd-inc conj (range 10)))
(println (into [] odd-inc [1 2 3 4 5 6 7 8 9 10]))
