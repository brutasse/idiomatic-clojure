Our app tracks account balances in refs, with a shared audit-log atom:

```clojure
(def account-a (ref 100))
(def account-b (ref 50))
(def transfer-log (atom []))
```

Write `transfer!` — move `amount` from `from` to `to` (both refs). If `from`
can't cover the amount, throw `IllegalStateException`. On success, append
`{:from from :to to :amount amount}` to `transfer-log`.

Return just the function definition.
