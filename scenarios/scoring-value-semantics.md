# Scoring rubric — value-semantics fixtures (equality + comparators guides)

## H (generation)
| # | Element | Pass | Fail |
|---|---|---|---|
| H1 | strip NaN readings | `NaN?` / `Double/isNaN` based filter | `(remove #(= % Double/NaN) …)` (no-op — NaN not = to itself) |
| H2 | "exactly full scale" on `(/ v max)` | `==`, compare to `1.0` with category awareness, or explicit note on int/float categories | `(= (/ v m) 1)` blind — false for the int/float split |
| H3 | top-3 with deterministic tie by id | explicit tiebreaker: `sort-by` keyfn returning vector `[(compare …)/(- score) id]` family, or two-pass stable sort, or comparator with id tiebreak | naive `(sort-by score >)` (disclosed or not); tie handled "by luck" |

## I (review — explicit correctness ask)
| # | Planted bug | Pass = caught AND fixed with | Fail |
|---|---|---|---|
| I1 | `remove #(= % Double/NaN)` no-op | `NaN?`/`Double/isNaN` + explanation | missed / "fix" still =-compares |
| I2 | `sort #(- %2 %1)` subtraction comparator, `:total` are decimals | `compare`-based (`sort-by :total >` or `#(compare (:total %2) (:total %1))`) + why | missed / kept with polish |
| I3 | `sorted-set-by` with `<=` boolean comparator + same-`:ts` events | `<`-style boolean or 3-way comparator; dedupe/lookup failure explained | missed |
| I4 | `ArrayList` keys in hash-map, vector lookup → nil | `(vec coords)` at the boundary (or key on `str`/vector), hash/= inconsistency explained | missed |
| I5 | `(= (/ reading max-val) 1)` | `==` + numeric-category explanation | missed |
| I6 | decoys: correct `==` usage, vector/list `=` sanity | left alone | "fixed" `==`→`=`, or broke the decoys |

Knowledge probe, not framing probe: the prompt explicitly asks for
correctness. Contamination irrelevant — none of this ground is in the skill.
Expected hard ones: I3, I4. Each confirmed miss sizes the GREEN row.
