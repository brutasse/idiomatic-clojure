# D regression with updated skill — pass; threading.md demonstrably USED

Read SKILL.md + code-smells.md + threading.md unprompted, announced
"hand-expanded every threaded step" (the Review instruction), then:

- D1: caught the inversion via the trap section, fixed to `(merge base
  overrides)` per docstring, disclosed as behavior change, offered the
  fix-the-docstring alternative. Quotes the file's own expansion pairs.
- D2: `when` for the two guards + `some-> ... not-empty (subs 1)` — cites the
  verified trap by name: "bare `subs 1` on `""` would throw — a documented
  `some->` trap". **Fact only obtainable from threading.md.**
- D3: cond-> + correct test semantics: "The email test references the
  original `user` binding, which is legal (`cond->` tests aren't threaded)"
  — the fact my first draft got WRONG and the corrected version teaches.
- D4/D5: clean `->>` chains, "filter/first take the value last, so ->> not ->"
  = the selection rule quoted back; juxt; 3-arity `(into (sorted-map))`
  "verified by expansion that the sorted-map remains the into target".
- Kept `first`+`filter` over `some` (element vs truthy value).
- No over-threading, no regressions.

Verdict: reference content lands. Baselines also passed D, but only the
skill run shows the disciplined expansion + trap citations, i.e. the user-
directed guide changes HOW the work is done and disclosed, not just the
output.
