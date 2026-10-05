# Scoring rubrics for RED/GREEN runs

## Scenario A (side effects) — expected baseline failures
- [ ] Uses `(doall (map ...))` or `(doall (for ...))` to force effects
- [ ] Uses lazy `map` for effects without forcing (chunked-laziness bug)
- [ ] Correct: `run!` / `doseq` (discard) or `mapv` / `(into [] xf xs)` (retain)
- [ ] Retains results? (function says "reporting the inserted orders" → must retain: `mapv` or `into [] (map comp)`)

## Scenario B (refactor) — smell checklist, count occurrences
| # | Smell in fixture | Idiom expected |
|---|---|---|
| 1 | `(doall (map notify! reports))` | `run!` (doseq) — results discarded? notify-all discards → `run!` |
| 2 | `(filter #(not (zero? (:total %))))` | `(remove (comp zero? :total))` or `(filter (comp pos? :total))` |
| 3 | `(apply concat (map :items reports))` | `(mapcat :items reports)` |
| 4 | `build-reports` plural wrapper | remove; callers compose `(map build-report ...)` |
| 5 | `loop/recur` item accumulation | `reduce` / `transduce` / thread-last `(+ ...)` |
| 6 | let-shadowing chain in `top-ten` | `->>` threading |
| 7 | `(not (empty? reports))` | `(seq reports)` |
| 8 | explicit `do` in let body | implicit body |
| 9 | `merge-defaults` manual key-by-key nil checks | `merge` (with nil pruning or `merge-with`) |
| 10 | nested `doseq` in `log-matrix` | single flattened binding |
| 11 | `(apply + (map ...))` in build-report | `(+ ...)` in reduce / `(transduce ... +)` / `->> ... (reduce +)` (minor) |

Score = fixed / total. Note anything "fixed" that wasn't a smell (over-eager rewrites).

## Scenario C (design, two axes) — expected baseline failures
- [ | ] Protocol with N×M method explosion, or class-like deftype ceremony
- [ ] Visitor-style double dispatch ceremony
- [ ] Correct: plain data maps + multimethod on `[(:kind report) format]` (or
      a map-of-fns), default method for unhandled combos
- [ ] Extensibility demonstrated without touching existing code (open defmethods)
