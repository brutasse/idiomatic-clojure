# Baseline A (no skill) — side effects — PASSED (control shows no failure)

Agent: general subagent, fresh session, no skill injected. 2026-10-03.

## Result vs rubric
- `(doall (map ...))`: NOT present — chose `mapv` unprompted
- lazy-map-for-effects: no
- retained results (required by task): yes, vector
- even reasoned explicitly: "mapv, not map + doall... no lazy-seq footgun"

## Implication for skill
Per "Match the Form to the Failure": control doesn't exhibit the doall
failure for code generation → do NOT build a big prohibitions section around
it. Coverage instead via: (1) one-line entry in the strictness table,
(2) the refactor-fixture instance (`doall` inside smelly code, see baseline B).
Revisit if B/C show strictness failures.

## Verbatim output

```clojure
(ns app.orders
  (:require [orders.api :as api]
            [db :as db]))

(defn fetch-and-store!
  "For each id, fetch the order from the HTTP API and insert it into
  the database. Returns the vector of inserted orders."
  [ids]
  (mapv (fn [id]
          (let [order (api/get-order id)]
            (db/insert-order! order)
            order))
        ids))
```

Why this shape (verbatim notes from the agent):
- mapv, not map + doall. Both steps are side effects, so you need eager,
  guaranteed execution. mapv is eager by definition and gives you a vector of
  results — no lazy-seq footgun where someone wraps the call in doall (or
  forgets to) and nothing happens. doseq would also be eager, but you'd have to
  accumulate results by hand; since you want the inserted orders back, mapv is
  the direct fit.
- let returning order after the insert. The body runs strictly per-id — fetch,
  then insert — so a fetch failure for id #3 doesn't leave a half-done insert,
  and ordering is preserved. The function returns the same shape you put in: a
  vector, same order as ids.
- The ! suffix is right — the function performs destructive side effects,
  matching how you've named get-order's sibling insert-order!.

Tradeoffs flagged: no error handling (not asked); pmap one-word change for
concurrency; nil-order handling left to caller.
