# Choosing and checking threading macros

Construction guide (based on the official
[threading macros guide](https://clojure.org/guides/threading_macros)).
Read before threading anything beyond a trivial chain, and when reviewing
threaded code.

## Which macro — decide by argument position

Ask, per step: *where does the threaded value go in this call?*

| Step functions take the value as… | Use | Examples |
|---|---|---|
| first argument | `->` | `assoc` `update` `dissoc` `get` `get-in` `assoc-in` `update-in`, Java receivers |
| last argument | `->>` | `map` `filter` `remove` `reduce` `take` `sort-by` `into` `group-by` `distinct` |
| any/named position, or used twice | `as->` (name it, keep the chain short) | `(as-> xs v (interleave v ys) (take 5 v))` |
| first, only while non-nil | `some->` | `(some-> m :path Long/parseLong inc)` — also good for one guarded call, no threading needed |
| last, only while non-nil | `some->>` | `(some->> m :items (filter even?) first)` |
| first, conditionally | `cond->` | optional `assoc`/`update` steps |
| last, conditionally | `cond->>` | optional `filter` steps in a seq pipeline |
| side-effecting builder calls, want the object back | `doto` | `(doto (StringBuilder.) (.append "a") (.append "b"))` |

Bare symbols/keywords thread as unary calls: `(-> person :hair-color name)` —
prefer bare steps over `(f ,,,)` noise. The guide's `,,,` insertion markers
are a teaching aid; don't ship them in code.

`cond->`/`cond->>` never short-circuit: every test is evaluated, in order,
between the steps (so test side effects all run). And tests are **not**
threaded — a test expression can only reference surrounding bindings, never
the value threaded so far: `(let [n 1] (cond-> n true (inc) (= 2 n) x))`
skips `x` even though the threaded temp is `2` by then. If a predicate needs
the intermediate value, use `as->`.

## Trap: position is meaning

First and last insertion are only equivalent when the other args don't care.
For functions whose argument positions carry semantics, the macro choice
**silently changes behavior** — no error, both forms "look" idiomatic:

```clojure
(->  base (merge overrides))  ;; (merge base overrides)     — overrides win
(->> base (merge overrides))  ;; (merge overrides base)     — base wins  ⚠
(->  xs (into ys))            ;; (into xs ys)
(->> xs (into ys))            ;; (into ys xs)               — target flipped ⚠
(->  xs (concat ys))          ;; (concat xs ys)
(->> xs (concat ys))          ;; (concat ys xs)             — order flipped ⚠
```

Same class: `interleave`, `dedupe`-pairs, any `-in` function where `->>`
would put the path first, two-sequence predicates.

**Checking rule:** when threading an existing call (either direction), expand
each step by hand. If the function takes more than "collection + rest" — two
maps, two seqs, a path and a value — state which argument wins and keep the
threader that keeps the intended one. When in doubt, don't thread the step.

## Flattening nil chains (when-let ladders, interop)

`some->`/`some->>` stop at the first `nil` — every remaining step must accept
the non-nil value. Watch the two ways a flattened chain goes wrong, both
REPL-verified on 1.12:

1. **Crash on empty**: `some->` short-circuits on `nil` only — `""` sails
   through, and `(subs "" 1)` throws `StringIndexOutOfBoundsException`. End
   the chain with `not-empty` when empty should mean "stop":

   ```clojure
   (some-> user :note str/trim not-empty (subs 1))  ;; "" or nil → nil
   ```

2. **Silent wrong value**: many helpers are nil-tolerant and hide a dropped
   guard instead of crashing — `(count nil)` → `0`, `(rest nil)` → `()`,
   `(str/join (rest nil))` → `""`. A "simplification" that drops the `when-let`
   often returns `""`/`0` where callers expect `nil`. Genuinely nil-intolerant:
   `pos?`/`zero?`/`neg?`, `inc`/`dec`, `subs`, interop methods (`nil` receiver).

```clojure
;; (when-let [a (f x)] (when-let [b (g a)] (.m b)))  →  some->
(some-> x f g (.m))            ;; ✓ bare interop step; nil stops the chain

;; multi-source guards (country AND phone) don't fit single-value threading:
;; keep the explicit `when`, then some-> the derived value.
(when (and country phone)
  (some-> (store/format-phone country phone) not-empty (subs 1)))
```

And `(first (filter p xs))` is **not** `(some p xs)` when you need the
element: `some` returns the predicate's truthy *value* — `true` for a keyword
predicate, not the item.

## Reading threaded code in review

Expand the first and last step of every `->`/`->>`/`cond->>` by hand. Wrong
macro, wrong winner, or a `nil` step outside its chain are the three failures
this file exists to catch.
