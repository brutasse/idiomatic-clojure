(ns parse-port.core
  (:require [clojure.string :as str]))

(defn parse-port
  "Returns the TCP port (1–65535) contained in the file at `path` as an integer.
   Throws `IllegalArgumentException` if the file does not contain a valid port."
  [path]
  (let [content (str/trim (slurp path))
        port (try (Integer/parseInt content) (catch NumberFormatException _ nil))]
    (if (and port (<= 1 port 65535))
      port
      (throw (IllegalArgumentException. (str "Invalid port in " path ": " content))))))

(defn slugify
  "Returns a lowercase version of `s` with runs of whitespace replaced by a single hyphen."
  [s]
  (str/lower-case (str/replace s #"\s+" "-")))

(defn truncate
  "Returns the first `n` characters of `s`."
  [s n]
  (subs s 0 n))
