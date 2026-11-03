(ns bands.core
  "Create music bands and report which of them are loud."
  (:require [clojure.string :as str]))

(defrecord Band [name genre])

(defn make-band
  "Create a `Band` from `name` and `genre`.
   `name` must be a non-empty string, `genre` a keyword.
   Throws `IllegalArgumentException` otherwise."
  [name genre]
  (when-not (string? name)
    (throw (IllegalArgumentException. "name must be a string")))
  (when-not (seq name)
    (throw (IllegalArgumentException. "name must not be empty")))
  (map->Band {:name name :genre genre}))

(defn- loud-genre?
  "Whether `genre` is one of the loud genres."
  [genre]
  (contains? #{:metal :punk} genre))

(defn loud?
  "Whether `band` is loud. Metal and punk bands are loud."
  [band]
  (loud-genre? (:genre band)))

(defn ^{:deprecated true :superseded-by "loud?"} shout!
  "Return the name of `band` in upper case."
  [band]
  (str/upper-case (:name band)))
