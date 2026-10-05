# Baseline D rep 2 (no skill) — PASS ~6/6 — RETRACTS this file's first version

## Retraction
An earlier delivery for this session (same label, different content: claimed
`(->> base (into overrides))` re-threading, a crash-y `str/join (rest …)`
suggestion) was NOT the final/real transcript — this one is. Scoring of D1
"MISSED" and D2 "crash" was wrong on both facts and is retracted. Two extra
errors in my scoring at the time, caught by the REPL:
- `(str/join (rest nil))` → `""`, not a crash (`count nil` → 0, `rest nil` → ())
- `pos?`/`zero?`/`inc`/`subs` DO NPE on nil, but `count` doesn't — so the
  original `(when (pos? (count x)))` ladder was already nil-safe via count.

## Actual rep 2 scorecard
| # | Element | Result |
|---|---|---|
| D1 | ->> / merge precedence | ✅ FLAGGED: "base beats overrides on shared keys, which reads opposite to the docstring"; preserved behavior (task said correct), offered `(merge base overrides)` if docstring governs — the surgical-correct choice |
| D2 | when-let ladder + interop | ✅ `(when (and country phone) (let [f (store/format-phone country phone)] (when (seq f) (subs f 1))))` — `seq` guards nil AND empty; `.substring` → core `subs` |
| D3 | let-rebind + if | ✅ cond-> (same benign `:email user`-original nit as rep 1) |
| D4 | inside-out nest | ✅ clean ->> bare-step chain |
| D5 | seq pipeline | ✅ composed transducer into 3-arity `into` |
| D6 | over-threading | ✅ "the threading was noise" — DE-threaded merged-view to plain `(merge overrides base)` |
| bonus | — | rejected `(some :active? xs)` for `(first (filter …))`: returns the truthy predicate value, not the element — tested-then-declined |

# CORRECTED D RED SUMMARY
D1: caught 2/2. D2: handled 2/2. D3–D6: pass 2/2. Combined with E (2/2):
NO threading failure observed in any baseline → per "Match the Form to the
Failure," no failure-driven guidance is warranted. references/threading.md
proceeds as USER-DIRECTED construction reference (declared caveat below),
tested as a REFERENCE skill: retrieval accuracy + snippet correctness
(nREPL-verified), not pressure scenarios.
