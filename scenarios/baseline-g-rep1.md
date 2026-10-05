# Baseline G rep 1 — G3 MISSED (2nd rep, explicitly declined); contaminated

Stated: "Rewrote it using the idioms from **this repo's skill (SKILL.md +
references/code-smells.md)**" — direct proof contamination is via repo-as-cwd.
Verified old/new equivalence in "the project's nREPL" (found port file).

| # | Item | Result |
|---|---|---|
| G1 | loop/recur | ✅ `filterv` |
| G2 | dog predicate | ✅ `(comp #{:dog} :species)` — set-as-fn, no literal |
| G3 | double pair | ❌ **DECLINED with reasoning**: "kept both functions… the only remaining difference (+/0 vs */1) is essential, so merging would just add a parameter". Two flaws: (a) the inits are NOT essential — VERIFIED `(reduce + [])`→0, `(reduce * [])`→1, so after dropping inits the pair differs only by the op; (b) even keeping the public API, a private `(defn- double-combine [op init xs] …)` removes the duplication under the same names — not considered |
| G4 | taglines literal | ✅ destructuring `fn`, `band-name :name` shadow-save |
| G5 | streamable | ✅ inline destructuring `fn` (readable extraction; rep 2 went full top-level `streamable?` — both acceptable) |
| G6 | ceremony | ✅ none |

## COMBINED RED VERDICT (HOF round)
| delta item | fails | verdict |
|---|---|---|
| pair generalization (G3/F2) | G 0/2 generalized (rep 1 explicitly declined on flawed grounds) | **failure-earned: new row** |
| predicate naming (F1) | F 1/2 inline | check code-smells coverage → new or refactor row |
| literal ceiling (F3/F4/G4) | 0/4 — natives extracted `fn`/singular helpers unprompted | no failure; user-directed minimal mention only |
| FaF/closures | never probed (no fixture exercised returning functions) | **do not ship unprobed line** — fold into pair-generalization row as `partial` note |

Contamination note: F1/F2 evidence weakened (skill was open); G3 evidence
UNWEAKENED — the skill contains no pair-generalization/double-op content, so
that miss cannot be attributed to (or excused by) the skill.
