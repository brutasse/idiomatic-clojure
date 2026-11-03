# GREEN J rep 4 (with skill) — PASS; baseline FAIL rep repaired + J round summary

Baseline rep 4 wrote `(map (comp clojure.string/upper-case display-name))`
in the `->>` — the exact RED shape. Now: fn literal at the call site.

## Rubric

| item | result |
|---|---|
| no `comp` at call site | ✓ — `#(clojure.string/upper-case (display-name %))` |
| `odd-inc` as named `comp` pipeline | ✓ — `def` (not `defn`), correct: `comp` over transducers *is* a transducer |
| nREPL verification | ✓ — `(loud-active)` → `("KOWELSKI [:ROCK]" "MASH [:METAL]")`; `(into [] odd-inc [1..6])` → `[2 4 6]`; composes under `transduce` |

```clojure
(defn loud-active
  "Upper-cased display names of the active bands."
  []
  (->> bands
       (filter active?)
       (map #(clojure.string/upper-case (display-name %)))))

(def odd-inc
  "Transducer that keeps odd numbers and increments each."
  (comp (filter odd?) (map inc)))
```

Notes: used fully-qualified `clojure.string/upper-case` because `band.db`
has no `:require` for `clojure.string`; offered the `str/upper-case`
variant if the ns gains the alias.

---

## J cross-rep convergence (all 5 GREEN reps in)

| contested call | baseline (5 reps) | GREEN rep1 | rep2 | rep3 | rep4 | rep5 |
|---|---|---|---|---|---|---|
| call-site composition | **`comp` 3/5**, literal 1/5, neutral 1/5 | neutral | literal | literal | literal | literal |
| `odd-inc` (named transducer) | `comp` 5/5 | `comp` ✓ | `comp` ✓ | `comp` ✓ | `comp` ✓ | `comp` ✓ |

## J ROUND FINAL

| item | RED | GREEN |
|---|---|---|
| `comp` at the call site | 3/5 (reps 2, 4, 5) | **0/5** — all three FAIL reps repaired |
| fn literal at call site | 1/5 | 4/5 |
| neutral (two plain `map`s) | 1/5 | 1/5 (rep1) |
| `odd-inc` = named `comp` | 5/5 (correct, must not regress) | **5/5 — no regression** |

**VERDICT: GREEN.** The house resolution ("fn literal beats `comp`/`partial`/
`complement` at call sites; `comp` reserved for named transducer pipelines")
closed the RED failure and did not regress the `odd-inc` shape every baseline
rep got right.

**Variance (no new rule warranted):** rep1 stayed neutral (two separate
`(map …)` steps) while reps 2–5 used a fn literal. The rule bans `comp` at
the call site; it does not mandate a literal over plain sequential maps.
Both are acceptable — adding a rule to force the literal would be bloat.
rep1's `str/upper-case` without a `:require` is a minor artifact of the
prompt excluding the ns form, not a house-rule violation.
