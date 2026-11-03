# GREEN J rep 2 (with skill) — PASS; baseline FAIL rep repaired

Baseline rep 2 wrote `(map (comp clojure.string/upper display-name) …)` —
the exact RED shape. Now: fn literal in execution order, and the rep
explicitly cites the house resolution ("`comp` inverts execution order and
is reserved for named transducer pipelines").

## Rubric

| item | result |
|---|---|
| no `comp` at call site | ✓ — `#(str/upper-case (display-name %))` |
| `odd-inc` as named `comp` pipeline | ✓ |
| nREPL verification | ✓ — `seq?` check; `odd-inc` on `[1..6]` → `[2 4 6]`; notes the reversed composition would give `[3 5 7]` |

```clojure
(defn loud-active
  "Uppercased display names of the active `bands`."
  []
  (->> bands
       (filter active?)
       (map #(str/upper-case (display-name %)))))

(def odd-inc
  "Transducer that keeps only the odd numbers, then increments each."
  (comp (filter odd?) (map inc)))
```

Notes: correctly flags that `str/upper-case` needs `[clojure.string :as
str]` in the ns (prompt excluded the ns form).
