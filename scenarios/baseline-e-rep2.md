# Baseline E rep 2 (no explicit skill) — PASS, with contamination caveat

Clean generation: `cond->>` for the optional min-score filter step (correct
last-position threading — the guide's `cond->>` used unprompted), `some->` for
footer trim, `->>` pipeline, `:or` defaults preserving false/0, `mapv` eager.

## METHODOLOGY NOTE — skill contamination
Rep 2 said "per the house rule on descending sorts" — it loaded
`writing-idiomatic-clojure` from ~/.agents/skills on its own (the subagent
harness shares the user skill list; my prompt never mentioned it).

Implication for RED integrity:
- E "baselines" are not uncontaminated. But contamination pushes toward
  PASSING, and both reps passed → the conclusion "no generation-with-threaders
  gap" is unaffected (if anything the skill deserves credit, not the agent).
- D fixtures remain a valid probe for THIS edit: the deployed skill contains
  nothing about merge/->> precedence, cond-> short-circuiting, or as->
  selection — the exact ground the new threading.md would cover. Any D bug
  detection is native competence, not the skill.

If D shows failures requiring new guidance, GREEN runs need the *updated*
skill text (fine) and, for a fair before/after claim, note the old skill was
auto-visible too.
