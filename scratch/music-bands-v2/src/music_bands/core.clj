(ns music-bands.core
  "This library manages music bands: `make-band` creates a `Band` and
   `loud?` reports which bands are loud."
  (:require [clojure.string :as str]))

(defrecord Band [name genre])

(defn make-band
  "Returns a `Band` for `name` and `genre`.
   Throws `IllegalArgumentException` if `name` is not a non-empty string."
  [name genre]
  (when-not (and (string? name) (seq name))
    (throw (IllegalArgumentException.
            (str "`name` must be a non-empty string, got: " (pr-str name)))))
  (->Band name genre))

(defn- loud-genre?
  "True if `genre` is one of the loud genres: metal and punk."
  [genre]
  (#{:metal :punk} genre))

(defn loud?
  "True if `band` is loud: metal and punk bands are."
  [band]
  (loud-genre? (:genre band)))

(defn ^{:deprecated true :superseded-by "loud?"} shout!
  "Returns the name of `band` in upper case."
  [band]
  (str/upper-case (:name band)))
