# GREEN B rep 3 (with skill, post-REFACTOR) — tie clause verified, 3/3 overall

The REFACTOR fix works. Rep 3:

```clojure
(defn top-ten [reports]
  ;; ties decide which reports make the cut, so keep stable sort + reverse
  ;; rather than (sort-by :total >), which would reverse tie order.
  (let [top (->> reports (active-reports) (sort-by :total) reverse (take 10))]
    ...))
```

- Cited the new house rule verbatim AND encoded it as a code comment so a
  future "simplifier" can't undo it. Convergence achieved (rep1 kept it
  silently, rep2 flipped it silently, rep3 keeps it with reason — variance
  collapsed into informed choice).
- build-reports DELETED (plural rule 3/3 GREEN vs 0/2 baseline).
- notify-all! KEPT this rep with `run!` + correct discrimination: "it's a
  batch of effects, not a pure-data plural map wrapper" — the wrapper rule
  now shows bounded, reasoned application, not deletion mania.
- House merge-with + escape-clause disclosure again. All 11 rubric items
  green. Self-verified: 9 pure fns differential-tested incl. sort ties.

# FINAL TEST MATRIX
| run | no skill | with skill |
|---|---|---|
| A generation (effects) | pass (1/1) | pass (1/1) |
| B refactor | 10/11 ×2, plural missed 2/2 | pass 3/3, contested calls converged |
| C design | pass (already knew it) | pass + rule cited |
| REFACTOR tie-clause addendum | — | verified, rep 3 |

DEPLOYED: symlinked to ~/.agents/skills/writing-idiomatic-clojure
