# GREEN I rep 1 (updated skill) — 5/5; RED-miss rep's twin now diagnoses to hash values

The task whose RED counterpart ("no conversion needed: they hash and compare
equal to Clojure vectors") shipped a false invariant now returns:

> "I confirmed a 2-entry map hit; at 10 entries it's a real hash-map and a
> vector lookup returns nil, because vector `hasheq` (Murmur, 156247261) ≠
> ArrayList.hashCode (994) even though they're `=`"

— including the exact hash values from scenarios/verified-snippets.md.
`(vec coords)` on **both** sides, bolded. RED failure mode closed.

Also: all other plants caught (NaN? with nil caveat, == categories,
truncation-to-int with the [1.0 0.9 …] repro, dual comparator bug separated);
tie clause upgrade applied ("put a tiebreaker in the key: [(- total) (:id
r)]"); tautology flagged WITHOUT speculative rewrite, asks for author intent
(cleanest handling of the four runs); orphan :import removal attributed.

## ROUND FINAL
| item | RED | GREEN |
|---|---|---|
| I4 hash-key trap | 1/2 missed + false invariant shipped (and fooled my initial plant) | **2/2 with array-map-shield reasoning** |
| everything else | 9.5/10 native | 2/2 unchanged |
| tie clause (upgraded) | rep REDs used reverse fallback | 2/2 new key-vector form unprompted |

Rows shipped: 1 failure-earned (interop keys, with the ≤8-entry nuance) +
6 user-directed minimal (the guide's other traps; detection was native but
the rows encode verified numbers and the two-sided fix). SKILL.md 500 words.
