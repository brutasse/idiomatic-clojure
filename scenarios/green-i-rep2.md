# GREEN I rep 2 (updated skill) — 5/5 + array-map shield + upgraded tie clause

| # | Item | Result |
|---|---|---|
| I1 | NaN | ✅ `remove NaN?`, no-op verified |
| I5 | =/== categories | ✅ `==` direct, per-category Why quoted |
| I2 | subtraction | ✅ sort-by + truncation-to-int explanation ("10.5/10.2/10.9 all-equal, verified") |
| I4 | java-list keys | ✅ **the RED miss, closed with the row's exact nuance**: "Under 9 entries it's an array-map that scans by equals and *works* — hiding the bug until the map grows past 8, when every lookup returns nil (verified)" → `(vec coords)` **both sides**, bolded; tested past 8 entries |
| I3 | comparator dual bug | ✅ separates inconsistent-`<=` (contains? false for every member) from partial-key dedupe (ts-100 vanishes) — the two new rows applied as distinct defects |
| tie clause | upgraded | ✅ "Per the house rule this assumes ties are interchangeable — if a deterministic winner is needed, put the tiebreaker in the key: `(sort-by (fn [m] [(- (:total m)) (:id m)]) report)`" — NEW clause wording |
| tautology decoy | ✅ flagged, declined to guess-fix (cleaner than RED rep 2's speculative rewrite) |
| hygiene | ✅ orphaned `:import` removal attributed to own change; 3-arity into |

15 self-run checks. RED failure mode (rep 1's "no conversion needed" false
invariant) cannot recur from this distribution: the shield nuance is now
IN the row the agent quoted.
