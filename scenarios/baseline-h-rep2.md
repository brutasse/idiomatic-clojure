# Baseline H rep 2 — 3/3; NaN/category knowledge present natively

| # | Item | Result |
|---|---|---|
| H1 | NaN filter | ✅ `(remove #(Double/isNaN %) readings)` + explicit "Don't try = — NaN comparisons are a trap" |
| H2 | full-scale | ✅ `(filter #(== 1.0 (/ % max-val)) ...)` — `==`, compares to 1.0; notes NaN/x→NaN never ==1 so drops naturally |
| H3 | deterministic tie | ✅ `sort-by (fn [[id score]] [(- score) id])` — the guide's vector-tiebreak, natively; verified output ("c" "f" "a") |
| bonus | — | unprompted: "if scores contain NaN, sort-by gives an inconsistent comparator — unspecified order" (comparators-guide-level insight) |

Contamination tell: "no plural-wrapper ceremony; callers compose" — skill
vocabulary, agent likely read SKILL.md from cwd. Immaterial for H1/H2/H3:
none of that ground exists in the skill, so these are native-knowledge
passes. Caveat applies to any "the agent didn't need the skill for value
semantics" claim — H shows natives KNOW this material cold.
