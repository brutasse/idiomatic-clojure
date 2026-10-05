(require '[bandmanager.core :as b])

(def bands
  [{:name "Kowelski" :genre :rock :active? true :members 4 :score 87 :tags [:raw :polish]}
   {:name "Low Frequency" :genre :metal :active? true :members 2 :score 95 :tags [:fast]}
   {:name "Mash" :genre :metal :active? true :members 5 :score 91 :tags [:sludge :raw]}
   {:name "Silent" :genre :rock :active? false :members 6 :score 99 :tags [:quiet]}
   {:name "Glitterball" :genre :disco :active? true :members 8 :score 90 :tags [:glitter]}])

(defn check [label actual expected]
  (println (if (= actual expected) "PASS" "FAIL") label)
  (when-not (= actual expected)
    (println "  actual:  " actual)
    (println "  expected:" expected)))

(check "top-bands"
       (map :name (b/top-bands bands))
       ["Mash" "Kowelski"])

(check "taglines"
       (b/taglines bands)
       ["kowelski :: raw · polish"
        "low frequency :: fast"
        "mash :: sludge · raw"
        "silent :: quiet"
        "glitterball :: glitter"])

(check "zip-taglines"
       (b/zip-taglines ["Kowelski" "Mash"] [[:raw :polish] [:sludge :raw]])
       ["kowelski :: raw · polish" "mash :: sludge · raw"])

(check "boost-scores"
       (mapv :score (b/boost-scores 2 bands))
       [174 190 182 198 180])

(check "boost-members"
       (mapv :members (b/boost-members 0.5 bands))
       [2.0 1.0 2.5 3.0 4.0])

(check "boost-scores is non-mutating / vector"
       [(vector? (b/boost-scores 2 bands)) (:score (first bands))]
       [true 87])
