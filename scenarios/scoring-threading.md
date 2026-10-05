# Scoring rubric — threading fixtures

## D (refactor app.profile) — planted elements & what counts as pass
| # | Planted element | Pass | Fail |
|---|---|---|---|
| D1 | **Silent swap**: `(->> base (merge overrides))` → `(merge overrides base)`, overrides LOSE | notices precedence inversion (docstring says overrides win), fixes to `(merge base overrides)` | keeps `->>` version / "improves" it further / misses |
| D2 | nested `when-let` ladder, mixed positions (`format-phone country phone` — two sources), ends in interop `.substring` on possibly-nil | some-> (possibly split with when-let for the two-source step) or as-> — nil-safe, flatter | keeps ladder / broken naive some-> (threads into wrong position, drops one source) |
| D3 | `let` rebind + `if`-wrapped assoc/update | `cond->` (correct: no short-circuit needed — independent conditions) | keeps if-nests / cond->> with map steps |
| D4 | inside-out nest, seq chain + interop | `->>` pipeline (find-users → filter → first → enrich → :display → upper-case as bare steps) | keeps nesting / wrong macro (-> on filter forms) |
| D5 | seq pipeline + into | `->>` … `(into (sorted-map))` 3-arity style, juxt OK | intermediate-collection soup |
| D6 | over-threading check | no trivial `(-> x f)`, no as-> where -> / ->> suffice, no macro-forced contortions | threads the unthreadable for style |

## E (generate render-summary) — expected shape
- config transform via `->` (assoc/update steps)
- record pipeline via `->>` (filter/take/score-sort ops last-position)
- `some->` or equivalent nil-handling for the possibly-nil footer
- passes: correct macro per argument position without being told; fails:
  `->` on seq ops (arity errors) or `->>` on assoc-ops (wrong position),
  nil crash paths, deeply nested leftover where a threader fits.
