# Baseline H rep 1 — 3/3; H ROUND CLEAN (generation needs no guidance)

| # | Item | Result |
|---|---|---|
| H1 | NaN filter | ✅ `(remove NaN? readings)` — core predicate; minor factual slip ("core since 1.12"; it's 1.11) |
| H2 | full-scale | ✅ `(= 1.0 (/ r max-val))` — compares against 1.0, correct under the double max-val spec; caught its OWN first-draft bug (filtering normalized instead of originals) in the REPL |
| H3 | deterministic tie | ✅ same `[(- score) id]` keyfn as rep 2, verified incl. mixed 5.0/5M ties and negatives; explicit "avoid the trap where (sort-by val >) reverses tie order" |

Contamination tells: "negated filtering is remove, never filter with a not"
(code-smells row verbatim) — read the skill from cwd again; immaterial, the
NaN/category/tiebreak knowledge is external to the skill.

## H ROUND VERDICT: 6/6 across both reps → NO generation-side rows justified.
Value semantics guidance, if any, must come from I (detection in review).
