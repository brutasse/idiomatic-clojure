(ns app.bands
  (:require [clojure.string :as str]))

(defn keep-dogs [animals]
  (loop [acc []
         remaining animals]
    (if (empty? remaining)
      acc
      (if (= :dog (:species (first remaining)))
        (recur (conj acc (first remaining)) (rest remaining))
        (recur acc (rest remaining))))))

(defn double-sum [nums]
  (reduce + 0 (map #(* 2 %) nums)))

(defn double-product [nums]
  (reduce * 1 (map #(* 2 %) nums)))

(defn taglines [bands]
  (map #(str (str/lower-case (:name %))
             " :: "
             (str/join " · " (map name (:tags %))))
       bands))

(defn streamable [bands]
  (filter #(and (:active? %)
                (>= (:members %) 3)
                (contains? #{:rock :metal} (:genre %)))
          bands))
