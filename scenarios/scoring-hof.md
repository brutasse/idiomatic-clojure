# Scoring rubric — HOF fixtures (delta items from clojure.org HOF guide)

## F (generation)
| # | Delta item | Pass | Fail |
|---|---|---|---|
| F1 | non-trivial predicate (active AND members>=3 AND genre in set) | extracted to named var (`streamable?`, or composed `every-pred`/comp) or clearly readable named form | 3-clause anonymous predicate buried inline in `filter` |
| F2 | `boost-scores`/`boost-members` pair differing only in key | shared generalized helper (key/factor as args) — the guide's `double-op` move | two copy-pasted defns differing only in one keyword |
| F3 | tagline builder (lower + join + str, nested) | `fn` form, named helper, or comp/juxt composition | giant `#(...)` with nested sub-forms |
| F4 | zip-taglines (two parallel collections) | `fn [n tags]` / destructured / named-fn `map` | `%1`/`%2`/`%&` monster literal |

## G (review/refactor)
| # | Planted element | Pass | Fail |
|---|---|---|---|
| G1 | loop/recur filter (guide's own keep-dogs) | `filter` (+ any predicate form) | keeps loop |
| G2 | inline `= :dog` predicate | named `dog?` var or `(comp #{:dog} :species)` — named/meaningful | keeps buried anonymous equality |
| G3 | `double-sum`/`double-product` pair | generalized with the combining fn as argument (`(reduce op init ...)`), or comp/partial formulation; duplication acknowledged and removed | pair left as-is |
| G4 | `taglines` nested single-% literal | `fn` form or named helper or composition | keeps/reshapes into another giant literal |
| G5 | `streamable` 3-clause inline predicate | named var / `every-pred` / readable extraction | keeps giant inline literal |
| G6 | over-correction check | no multimethod/protocol/deftype invented for this | ceremony added |

G1/G2 are ALSO regression probes for existing skill rows (loop smell already
in red flags). F/G failure on delta items = new guidance warranted; failure
on G1/G2 = binding problem in existing rows (REFACTOR, not new content).
