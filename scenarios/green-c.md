# GREEN C (with skill) — pass; skill rule cited by name, no regression

Same architecture as baseline C (two multimethods + canonical IR), but the
agent opened with it: "per the skill's rule for two independent variation
axes (Bridge, not Visitor: two multimethods over a canonical intermediate
representation — N+M methods, never N×M pairs)".

Design per skill recipe:
1. data: kind-tagged maps with literal examples in a comment block ✅
2. pure transforms; `reduce + 0M` (cited house summing rule) ✅
3. `report->data` (axis kind) + `encode` (axis format), additive defmethods
   in separate namespaces ✅
4. one effect at the edge: eager `write!` ✅
5. "callers compose multiplicity themselves (no plural wrappers)" ✅ — the
   wrapper rule generalized correctly to a fresh scenario.

Self-verified in project nREPL (kinds × formats); caught 3 of its own defects
(CSV dropped metadata; destructuring vs array-map; xml/emit :content nesting).
PDF/JSON encoder bodies not run (deps off classpath) — honestly disclosed.

No regressions vs baseline (which was already strong). C is green.
