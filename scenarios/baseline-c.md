# Baseline C (no skill) — two-axis design — PASSED (no ceremony, multimethods+data)

Agent: general subagent, fresh session, no skill injected. 2026-10-03.

## Result vs rubric
- Protocol/interface N×M explosion: NO
- Visitor ceremony: NO
- Data + multimethod dispatch: YES — `(defmulti document :report/kind)` +
  `(defmulti render (fn [_doc fmt] fmt))` joined by a canonical "document" IR
  → N+M implementations instead of N×M (stronger than mishadoff's one-axis
  answer)
- Open extensibility demonstrated in a separate ns with additive defmethods
- No `:default` on render — fails loudly on unknown format (defensible choice)
- Self-verified: built 11-test suite in scratch/export-design/ (in my repo!),
  incl. hand-rolled PDF writer validated with qpdf/pdftotext

## Implication for skill
- Generation of Clojure-native designs is NOT a model gap.
- patterns-in-clojure.md justified only as a DESIGN VOCABULARY (user phrases
  asks in OO terms: "Builder", "Observer") — keep it a compact table, no
  tutorials. RED evidence for per-pattern rows: none violated; keep rows only
  where the mapping is non-obvious (Visitor, Template, Observer, Memento).
- C's IR trick (two multi methods + canonical intermediate data) is the one
  design insight worth putting in SKILL.md verbatim — it beats the naive
  two-axis single-multimethod answer.

Full design output: see subagent session ses_efe2d036bffeYT2l36VsrK4xaa —
~150 lines, too long to restate; key shapes quoted above.

# OVERALL RED SUMMARY (4 runs, 3 scenarios)
Single consistent generation failure: PLURAL WRAPPER (missed 2/2 in B).
Observed inter-rep variance (fixable by house resolutions in skill):
- merge-defaults: merge-with (changes key set) vs named-helper per-key (preserves)
- sums: transduce vs reduce
- top-ten: ->> pipeline vs single let
Everything else in the bsless catalog and the pattern-dissolution knowledge:
already owned by the model. Skill must be SMALL and opinion-binding, not a
tutorial.
