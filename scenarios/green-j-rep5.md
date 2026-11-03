# GREEN J rep 5 (with skill) — PASS; baseline FAIL rep repaired

Baseline rep 5 wrote `(map (comp #(.toUpperCase %) display-name))` — the
exact RED shape. Now: fn literal at the call site, and the rep discloses
the fixture quirk unprompted.

## Rubric

| item | result |
|---|---|
| no `comp` at call site | ✓ — `#(clojure.string/upper-case (display-name %))` |
| `odd-inc` as named `comp` pipeline | ✓ |
| verification | ✓ — ran a scratch script: `(loud-active)` → `(KOWELSKI [:ROCK] MASH [:METAL])`; `odd-inc` via both `transduce` and `into` → `[2 4 6 8 10]` |

```clojure
(defn loud-active
  "Upper-cased display names of the active bands."
  []
  (->> bands
       (filter active?)
       (map #(clojure.string/upper-case (display-name %)))))

(def odd-inc
  (comp (filter odd?) (map inc)))
```
