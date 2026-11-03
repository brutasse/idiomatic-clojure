# GREEN K1 rep 1 (with skill) — PASS; full rubric compliance

Baseline rep 1 had correct placement but shipped `ref-deref` (does not exist
in 1.12.5) and `ref-set`. Now: plain `@` in-tx, `alter`, and the retry
rationale cited from the skill.

## Rubric

| item | result |
|---|---|
| `swap!` OUTSIDE `dosync` | ✓ — second body form, after the transaction |
| no `ref-deref` | ✓ — `(let [balance @from])` |
| `alter` over `ref-set` | ✓ — `(alter from - amount)` / `(alter to + amount)` |
| `IllegalStateException` on insufficient balance | ✓ — throws on `<` (equal balance still transfers) |
| audit entry only on success | ✓ — the exception path exits before the `swap!` |

```clojure
(defn transfer! [from to amount]
  "Move `amount` from `from` to `to`, appending `{:from from :to to :amount amount}` to `transfer-log`.

   Throws `IllegalStateException` if `from` cannot cover `amount`."
  (dosync
    (let [balance @from]
      (when (< balance amount)
        (throw (IllegalStateException.
                (str "Cannot transfer " amount " from account with balance " balance))))
      (alter from - amount)
      (alter to + amount)))
  (swap! transfer-log conj {:from from :to to :amount amount}))
```

Notes: cites the skill's "once per attempt" rationale for the placement —
exactly the mechanism proven deterministically in `scratch/stm-retry.clj`
(2 log entries, 1 committed change). The `(alter from - amount)` partial
application is a bonus: no fn literal needed.
