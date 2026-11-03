We have a small band catalog:

```clojure
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
```

Two things:

1. `loud-active` — returns a sequence of uppercased display names for the
   active bands, using `active?` and `display-name`.
2. `odd-inc` — a named transducer that keeps only the odd numbers, then
   increments each.

Return just the two definitions (no ns form needed).
