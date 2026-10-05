# GREEN A (with skill) — pass, no regression, no rule over-application

Agent read SKILL.md from disk, then produced:

```clojure
(defn fetch-and-store!
  "Fetch each order by id, insert it into the database,
  return the inserted orders."
  [ids]
  (mapv (fn [id]
          (let [order (orders.api/get-order id)]
            (db/insert-order! order)
            order))
        ids))
```

- Cited skill rules verbatim ("effects at the edges, eager, named with !",
  "never doall", "one thing at a time").
- IMPORTANT: when citing the plural-wrapper ban, correctly distinguished the
  boundary function from the banned pattern ("this composes two side-effecting
  steps into one boundary operation... individual steps remain single-item
  functions") → no over-application failure of the wrapper rule.
- Offered `(mapv #(doto (orders.api/get-order %) db/insert-order!) ids)` as
  equivalent shorthand; kept let version for readability.

Same output shape as baseline A (which also passed) → skill did not degrade a
known-good behavior. A is green.
