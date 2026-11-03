# GREEN J rep 1 (with skill) — PASS; neutral call site (no `comp`, no fn literal)

vs baseline rep 1: same neutral shape — two plain `map` steps under `->>`.
Baseline scored this PASS; the rubric bans `comp` at the call site, it does
not mandate a fn literal. `odd-inc`: `comp` ✓ (named transducer — must not
regress; all 5 baseline reps got it right).

## Rubric

| item | result |
|---|---|
| no `comp`/`partial` at call site | ✓ — `(map display-name)` + `(map str/upper-case)` |
| `odd-inc` as named `comp` pipeline | ✓ |

```clojure
(defn loud-active
  "Uppercased display names of the active bands."
  []
  (->> bands
       (filter active?)
       (map display-name)
       (map str/upper-case)))

(def odd-inc
  "Keeps only the odd numbers, then increments each."
  (comp (filter odd?) (map inc)))
```

Notes: `str/upper-case` uses the house alias without a `:require` — the
prompt excluded the ns form, so not a failure; fully-qualified would have
been safer (rep3/rep5 chose that). No verification run reported.
