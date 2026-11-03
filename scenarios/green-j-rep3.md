# GREEN J rep 3 (with skill) — PASS; fn literal + fully-qualified `upper-case`

## Rubric

| item | result |
|---|---|
| no `comp` at call site | ✓ — `#(clojure.string/upper-case (display-name %))` |
| `odd-inc` as named `comp` pipeline | ✓ |
| nREPL verification | ✓ — `(loud-active bands)` → `["KOWELSKI [:ROCK]" "MASH [:METAL]"]`; `(transduce odd-inc conj [1 2 3 4 5])` → `[2 4 6]` |

```clojure
(defn loud-active
  "Uppercased display names of the active bands."
  [xs]
  (->> xs
       (filter active?)
       (map #(clojure.string/upper-case (display-name %)))))

(def odd-inc
  (comp (filter odd?) (map inc)))
```

Notes: takes `[xs]` instead of the global `bands` (baseline rep 3 did the
same and was scored PASS — the prompt doesn't forbid a parameter). Justified
fully qualifying `clojure.string/upper-case`: aliasing `[clojure.string :as
str]` would shadow the core `str` the fixture's `display-name` uses.
Disclosed the fixture quirk (`[rock]` docstring vs real `[:ROCK]`).
