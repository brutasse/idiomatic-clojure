# Baseline K1 (no skill) — STM transfer! — 1/2 correct placement; RED confirmed

Agent: general subagent, fresh session, no skill injected. 2 reps,
2026-11-02. Prompt: `prompt-k1-stm.md` (refs `account-a`/`account-b`,
audit-log atom `transfer-log`; `transfer!` moves `amount` from `from` to
`to`, throws `IllegalStateException` if `from` can't cover it, appends an
audit entry on success).

## Scorecard

| rep | atom placement | compiles on 1.12.5? | other |
|---|---|---|---|
| 1 | ✓ outside `dosync`, with correct retry rationale | **NO** — uses `ref-deref`, which does not exist in 1.12.5 | `ref-set` instead of `alter` (minor row applies) |
| 2 | **✗ inside `dosync`** — while its prose claims it is outside | yes | `deref` inside tx ✓, `alter` ✓, retry rationale stated correctly in prose |

Rep 2's code/prose contradiction was confirmed by re-querying the
subagent session: "the `swap!` is the second body form of the
transaction… The prose was wrong; the code above is what was written."

## Verbatim outputs

Rep 1 (`ses_e6300bb3fffe3inGuWdsUap4h4`):

```clojure
(defn transfer! [from to amount]
  (dosync
    (let [from-bal (ref-deref from)
          to-bal   (ref-deref to)]
      (when (< from-bal amount)
        (throw (IllegalStateException. "Insufficient funds")))
      (ref-set from (- from-bal amount))
      (ref-set to (+ to-bal amount))))
  (swap! transfer-log conj {:from from :to to :amount amount}))
```

Rep 2 (`ses_e6300bb3fffdGvIawAB1g5WMyQ`):

```clojure
(defn transfer! [from to amount]
  (dosync
    (if (< (deref from) amount)
      (throw (IllegalStateException.
              (str "Cannot transfer " amount ": " from " has only " (deref from))))
      (alter from #(- % amount))
      (alter to #(+ % amount)))
    (swap! transfer-log conj {:from from :to to :amount amount})))
```

## Implications

- The atom-in-tx row is a **real fix**, not just an anchor: one of two
  reps wrote exactly the silent-bug shape (duplicate audit entries on
  retry) while *arguing for* the correct placement in prose — the
  failure mode is rationalization, so the skill row must be explicit and
  verifiable, and the GREEN rubric must check the code, not the prose.
- `ref-deref` (rep 1) is a hallucinated var — the gotchas row in
  `code-smells.md` covers it; in-tx reads are plain `deref`/`@`.
- `alter` over `ref-set` (rep 1) — the minor conventions row applies.
- Retry mechanics for the skill's claims were proven deterministically in
  `scratch/stm-retry.clj` (read-pin → concurrent commit → write → 2 log
  entries, 1 committed change; 5/5 identical) — see
  `verified-snippets.md`, rows `stm-retry`/`stm-block`.
