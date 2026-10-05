# Clojure code smells → idiomatic rewrites

Catalog for **reviewing** and for "make this idiomatic" requests: scan every
row against the code. For generating new code, most of these are already your
default — you don't need to read this file.

All examples run on Clojure 1.12.

## Mapping and iterating

| Don't | Do | Why |
|---|---|---|
| `(apply concat (map f xs))` | `(mapcat f xs)` | one concept, no intermediate |
| `(for [x xs] (f x))` | `(map f xs)` | `for` is a comprehension; a single binding + map is `map` |
| `(map #(f %) xs)` | `(map f xs)` | trivial lambda adds noise |
| `(filter #(not (p %)) xs)` | `(remove p xs)` | `remove` *is* negated filter |
| `(filter #(p (:k %)) xs)` | `(filter (comp p :k) xs)` | compose, don't rebind `%` |
| `(doseq [x xs] (doseq [y ys] ...))` | `(doseq [x xs y ys] ...)` | bindings nest for free |
| `(doseq [x xs] (swap! acc conj (f x)))` | `(into [] (map f) xs)` or `mapv` | accumulator-in-atom is a `reduce` wearing a costume |

## Emptiness and truth

| Don't | Do | Why |
|---|---|---|
| `(when (not (empty? x)) ...)` | `(when (seq x) ...)` | `empty?` is `(not (seq x))`; use `seq` directly. Works on `nil` |
| `(when (= 0 (count x)) ...)` | `(when (empty? x) ...)` | `count` walks lazy/infinite seqs |
| `(= nil x)` / `(= true x)` | `(nil? x)` / `(true? x)` | intent |
| `(if x true false)` | `(boolean x)` — `x` is already the answer | |
| `(= 0 x)` / `(> x 0)` / `(< x 0)` | `(zero? x)` / `(pos? x)` / `(neg? x)` | intent (note: `zero?` throws on non-numbers) |
| `(or x default)` to default nil-ish values | `(if (nil? x) default x)` | `or` also swallows `false` and `0` |

## Sequence navigation

| Don't | Do |
|---|---|
| `(first (first xs))` | `(ffirst xs)` |
| `(first (next xs))` | `(second xs)` |
| `(next (next xs))` | `(nnext xs)` |

## `into` and building collections

| Don't | Do | Why |
|---|---|---|
| `(into [] xs)` | `(vec xs)` | direct constructor |
| `(into #{} xs)` | `(set xs)` | ditto |
| `(into coll (map f xs))` | `(into coll (map f) xs)` | the 3-arity is transducing: no intermediate seq. Passing the *call* `(map f xs)` is the smell |
| `(mapv f xs)` for effects only | `(run! f xs)` | don't build results you throw away |
| building one collection then copying into another | build into the right one | `into`/`conj` pick the right add |

For map→map transformation: `(update-vals m f)` / `(update-keys m f)` (1.11+),
or `(reduce-kv (fn [acc k v] (assoc acc k (f k v))) {} m)` — not
`(into {} (map (fn [[k v]] ...) m))`.

## Maps

Maps are collections, not slots — a `nil` value and a missing key are
indistinguishable to `get`. Don't store `nil`s; prune at the boundary.

| Smell | Fix |
|---|---|
| map with `nil` values flowing into `merge`/JSON | `(into {} (remove (comp nil? val)) m)` — or `(clojure.set/remove-vals nil? m)`; **`remove-vals` is in `clojure.set`, not `clojure.core`** |
| manual key-by-key nil-merge | `(merge m1 (prune-nils m2))` — two readable transforms |
| `{:a (:a m1) :b (:b m2)}` manual key picking | `(merge (select-keys m1 [:a]) (select-keys m2 [:b]))` |
| `(let [m (assoc ...)] (if p (assoc m k v) m) ...)` conditional build-up | `cond->` |
| scanning a seq of maps for key value | index it: `(group-by :k xs)` then `contains?`/`get`; `clojure.set/index` for sets |
| `merge`/`select-keys` in a hot loop | keep them (clarity) but measure; alternatives: `assoc` chains, `transient`, or build the map in one literal |

## Side effects and strictness

| Don't | Do |
|---|---|
| `(doall (map side-effect! xs))` | `(mapv side-effect! xs)` (keep results) or `(run! side-effect! xs)` (discard) |
| `(doall (for [x xs] (side-effect! x)))` | `(doseq [x xs] (side-effect! x))` |
| `(doseq [x xs] (swap! results conj (f x)))` | `(mapv f xs)` / `(into [] (map f) xs)` |
| lazy seq escaping a boundary, hoping someone realizes it | return an eager `mapv`/`into` result from functions that act |
| `(doall (doseq ...))` | `doseq` is already strict and returns nil — `doall` of it is doubly wrong |
| `(pmap side-effect! xs)` for I/O | you don't control the concurrency; use explicit `future`s + pool, or core.async |

`map` for effects gives chunked laziness: how many effects fire when someone
`first`s the result is an implementation detail.

## Style: flow that should be data

| Smell | Fix |
|---|---|
| `(when t (do a b))`, `(let [x 1] (do a b))` — explicit `do` where body is already implicit | drop the `do` (`when`, `let`, `fn`, `try`, ... take bodies) |
| `(let [x (f x) x (g x)] ...)` shadowing bindings | `(-> x f g)` |
| nested `let` forms | one `let` with more bindings |
| `(-> x f)` | `(f x)` — trivial threading is noise |
| `(when-let [a (f0 x)] (when-let [b (f1 a)] ...))` | `(some-> x f0 f1 ...)` |
| threading a map through `(assoc ...)`/`(update ...)` steps | `->` (first arg); threading sequences through `(map ...)`/`(filter ...)` steps uses `->>` (last arg) |
| `f` calls `g` calls `h`, each wrapping the last | `(-> x f g h)` — compose at the call site, test each step |

## Functions and data

| Smell | Fix | Why |
|---|---|---|
| `(defn build-things [xs] (map build-thing xs))` — plural wrapper next to the singular | delete it; call `(map build-thing xs)` | the wrapper hardcodes *how many things* into the API, so callers can't compose, transduce, or reuse it singular; the plural quietly accumulates logic the singular never gets |
| `(defn f [a b c d e f g])` — arg explosion | take one map of options (or split the function) | few functions use all args at once |
| `[& {:keys [a b]}]` key-value rest args | explicit map arg `[{a :a b :b}]` or `opts` map | rest-args can't be passed around without `apply` gymnastics |
| returning `[ok-count err-count]`, reading `(first res)` | return `{:ok n :err m}` | indices with business meaning belong as keys |
| `(first (filter p xs))` when absence is an error | `(some p xs)`, or throw on missing | `(first ...)` conflates "absent" and "falsy" |
| a function over a collection that must run once, called with `[x]` | take the single thing | see plural wrappers |
| pair of defns differing only in an embedded operation (`double-sum` / `double-product`) | one function taking the operation as argument — a private helper plus a public pair built by `partial` | copy-pasted pairs drift; op-as-argument keeps the API and kills the duplication. Explicit reduce identities are free anyway (`(reduce + [])` → `0`, `(reduce * [])` → `1`) |
| multi-clause anonymous predicate inline in `filter` | name it (`defn streamable?`) or compose the parts with `every-pred` | a predicate is domain policy — named it is testable, reusable, greppable |
| `#(...)` literal with several `%n` or nested sub-forms | `fn` with destructured args, or a named singular helper | literals are for one-liners; past a single `%` the reader has to expand it twice |

## Value identity: `=`, hash, comparators

| Don't | Do | Why |
|---|---|---|
| `(remove #(= % Double/NaN) xs)` | `(remove NaN? xs)` | NaN is not `=` to itself — the `=`-filter removes nothing. `NaN?` is core (1.11+), NPEs on `nil` |
| `(= computed-int computed-float)` for numeric identity | `==` | `=` is per-category (integers/ratios vs floats vs BigDecimal): `(= 1 1.0)` → false; `==` is numeric-only and cross-category |
| subtraction comparator `#(- (f %1) (f %2))` | `(sort-by f >)` or `#(compare (f %2) (f %1))` | comparator results are truncated to int: decimal diffs vanish (`(sort #(- %1 %2) [1.0 0.9 0.8 0.7])` → descending), longs wrap or throw overflow, one NaN poisons the order |
| boolean comparator `#(<= (f %1) (f %2))` for `sort`/`sorted-set-by` | a `<`-like boolean, or 3-way `compare` | `<=` answers "before" in *both* directions for equal values — inconsistent order; `sorted-set-by` members then don't resolve on lookup |
| `sorted-set-by` comparator that compares only part of the value (`(compare (:ts a) (:ts b))`) | comparator over a key vector incl. tiebreaker: `(compare [(:ts a) (:id a)] [(:ts b) (:id b)])` | a sorted set dedupes comparator-equal entries — same-`ts` events vanish silently |
| nested `if (not= 0 (compare …))` ladder for multi-field sort | compare key vectors: `(compare [(:salary y) (:name x)] [(:salary x) (:name y)])` | vectors compare lexicographically; swapping x/y per field flips that field's direction |
| Java collection as a map key or set member (`(ArrayList. coords)` as key) | `(vec k)` / `(set k)` at the interop boundary, on **both** store and lookup sides | `=` is true across types but `hash` is not (vector `hasheq` is Murmur, Java collections use `hashCode`) → hash-map/hash-set misses. Array-maps (≤ 8 entries) scan by equals and *work*, hiding this until the map grows |
