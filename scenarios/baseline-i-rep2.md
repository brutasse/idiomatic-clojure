# Baseline I rep 2 — 5/5 + bonus depth

Caught all five plants, all REPL-verified by the agent:
- I1: `Double/isNaN`, disclosed nil-throw behavior change
- I2→sort-by: named the subtraction trap, NaN poisoning, plus the tie clause
  verbatim ("if ties ever need to be stable, use (reverse (sort-by :total))")
  — SKILL.md vocabulary; read the skill from cwd. Immaterial to I-knowledge
  (none of it is in the skill), noted for honesty.
- I3: distinguished `<=` (never returns 0 → duplicate identical events kept,
  size-2 "set") from `<` (ties collapse → ts-100 events silently dropped) —
  both verified; fixed with `[ts id]` vector comparator, 300-shuffle tested
- I4: caught the REAL issue — mutable ArrayList keys + type-agreement
  requirement — fixed with `(vec coords)` on both store and lookup sides
  (verified across vector/list/ArrayList lookups) + MapEntry 3-arity into +
  `name`-shadow catch
- I5: division→direct `==` (also flagged integer /0 ArithmeticException)
- Decoys: tautology reordered? flagged as needing author intent (didn't
  guess-fix silently); `==` usage left alone.

Detection total I-round: 9.5/10 across reps — only miss is I4 (rep 1), and
rep 2 caught it WITH the scale caveat ("lazy seq silently returned nil" —
tested a seq lookup against ArrayList keys). The value-semantics knowledge is
native; the residual gap is that it's not reliably APPLIED to interop
collections, and small-map demos mislead.
