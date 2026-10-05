(ns bandmanager.core
  (:require [clojure.string :as str]))

(defn top-bands
  "Active rock/metal bands with at least 3 members, best score first."
  [bands]
  (->> bands
       (filter (every-pred :active?
                           #(<= 3 (:members %))
                           (comp #{:rock :metal} :genre)))
       (sort-by :score >)))

(defn- tagline
  [{n :name tags :tags}]
  (str (str/lower-case n)
       " :: "
       (str/join " · " (map name tags))))

(defn taglines
  "Tagline strings for a collection of band maps."
  [bands]
  (mapv tagline bands))

(defn zip-taglines
  "Same taglines, from parallel collections of names and tag lists."
  [names tag-lists]
  (mapv (fn [n ts] (tagline {:name n :tags ts}))
        names
        tag-lists))

(defn- boost
  [k factor bands]
  (mapv #(update % k * factor) bands))

(defn boost-scores
  "Bands with each :score multiplied by factor."
  [factor bands]
  (boost :score factor bands))

(defn boost-members
  "Bands with each :members multiplied by factor."
  [factor bands]
  (boost :members factor bands))
