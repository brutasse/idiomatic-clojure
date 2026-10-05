# Baseline I rep 1 — 4.5/5; MISSED I4 with a WRONG-THEORY "fix"

Caught: I1 NaN (NaN? + nil-NPE guard), I5 `=`/`==` (verified `(= 1.0 1)`),
I2 subtraction ("throws long overflow on extremes" — true, and separately
silent int-truncation), I3 `<=` comparator (caught + self-warned the
partial-key merge trap: "the tempting fix (compare (:ts a) (:ts b)) silently
merges the ts-100 events"), tautology decoy, no over-fix of `==` decoy.

## I4 — the interesting miss
Declared the ArrayList keys "pure ceremony", removed wrapping, and stated in
the docstring: "Java paths need no conversion: they hash and compare equal to
Clojure vectors". **Half-true and verified half-true** — his test used a
1-entry map: PersistentArrayMap, linear scan by `equals` → works. At >8
entries (PersistentHashMap, bucketing by hash):

- `(.hashCode [1 2])` == `(.hashCode (ArrayList. [1 2]))` = 994 ✓ (his claim)
- `(hash [1 2])` = Murmur 156247261 ≠ `(hash ArrayList)` 994 → lookup nil

REPL-verified: `[ArrayList-key, 20 pad pairs] (m [1 2])` → **nil**. His
rewritten `route-lookup` fails at scale exactly like the original, and the
docstring asserts the false general rule. The array-map→hash-map growth
threshold is the crux of why this trap survives review: it works in the demo
and breaks in prod.

Also noteworthy: his NaN? nil guard — `(NaN? nil)` NPEs (verified), so
`(and (number? r) (NaN? r))` preserves the original `=`-tolerates-nil.

## Verdict
I4 (interop collections as hash keys, scale-dependent) = the round's real
failure: 1/2 missed, and the miss SHIPPED a false invariant in a docstring.
Failure-earned row, with the ≤8-entry nuance stated.
