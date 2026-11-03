(ns music-bands.core
  "Create music bands and report which ones are loud."
  (:require [clojure.string :as str]))

(defrecord Band [name genre])

(defn make-band
  "Create a band. name must be a non-empty string, genre a keyword."
  [name genre]
  (if (and (string? name) (not (empty? name)))
    (Band. name genre)
    (throw (IllegalArgumentException. "band name must be a non-empty string"))))

(defn- loud-genre?
  "True if genre is metal or punk."
  [genre]
  (contains? #{:metal :punk} genre))

(defn loud?
  "True if band plays a loud genre (metal or punk)."
  [band]
  (loud-genre? (:genre band)))

(defn ^:deprecated shout!
  "Deprecated: use loud? instead.
   Returns the band's name in upper case."
  [band]
  (str/upper-case (:name band)))
