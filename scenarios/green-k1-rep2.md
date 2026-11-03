# GREEN K1 rep 2 (with skill) — PASS; baseline RED-failure rep repaired + K1 round summary

Baseline rep 2 put `(swap! transfer-log conj …)` **inside** `dosync` while
its prose claimed it was outside — the exact silent-bug shape (duplicate
audit entries on retry). Now: `swap!` after the transaction, and the rep
**reproduced the retry mechanism itself** as verification.

## Rubric

| item | baseline | result |
|---|---|---|
| `swap!` OUTSIDE `dosync` | ✗ inside (with contradicting prose) | ✓ — second body form after `dosync` |
| no `ref-deref` | — (rep1 had it) | ✓ — plain `@from` |
| `alter` over `ref-set` | ✓ | ✓ — `(alter from - amount)` / `(alter to + amount)` |
| `IllegalStateException`, refs untouched on failure | ✓ | ✓ — verified: refs unchanged, **no log entry** on the failure path |
| boundary: balance == amount | — | ✓ — transfers |

Verification highlights (beyond the rubric): the rep forced a real retry —
a concurrent commit landing in the read window made the transaction
attempt twice — and observed exactly one committed ref change and exactly
one log entry. That is the same mechanism proven deterministically in
`scratch/stm-retry.clj` (2 attempts → 2 atom side effects, 1 committed
change), independently reproduced.

```clojure
(defn transfer! [from to amount]
  "Move `amount` from `from` to `to` and append the transfer to `transfer-log`.

   Throws an `IllegalStateException` if `from` can't cover `amount`."
  (dosync
    (when (< @from amount)
      (throw (IllegalStateException. (str "Cannot transfer " amount ": balance is " @from))))
    (alter from - amount)
    (alter to + amount))
  (swap! transfer-log conj {:from from :to to :amount amount}))
```

---

## K1 cross-rep convergence (both reps in)

| contested call | baseline rep1 | baseline rep2 | GREEN rep1 | GREEN rep2 |
|---|---|---|---|---|
| atom placement | ✓ outside | ✗ inside (prose said outside) | ✓ outside | ✓ outside |
| in-tx read idiom | ✗ `ref-deref` (absent) | ✓ `deref` | ✓ `@from` | ✓ `@from` |
| ref update | `ref-set` (minor row) | `alter` | `alter` | `alter` |
| retry rationale | stated | stated (contradicted by code) | cited from skill | **proven by forced retry** |

## K1 ROUND FINAL

| item | RED | GREEN |
|---|---|---|
| `swap!` outside `dosync` | 1/2 (rep2 failed, prose contradicting code) | **2/2** |
| no hallucinated `ref-deref` | 1/2 (rep1) | **2/2** |
| `alter` over `ref-set` | 1/2 | 2/2 |

**VERDICT: GREEN.** The baseline's rationalization failure mode (correct
prose, wrong code) is closed: both GREEN reps put the audit append after
the transaction, and rep2 went further and demonstrated the failure it
avoids. No rule change needed — the conventions.md row ("Never update an
atom inside a transaction… verified: deterministic retry → 2 log entries,
1 committed change") is stated at exactly the strength the evidence
supports.
