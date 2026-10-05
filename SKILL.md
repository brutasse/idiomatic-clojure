---
name: writing-idiomatic-clojure
description: Use when writing, reviewing, or refactoring Clojure or ClojureScript code — new functions or namespaces, "make this more idiomatic", designing modules with dispatch or extensibility — or when code shows Java-in-Clojure symptoms such as doall, loop/recur accumulators, plural wrapper functions, protocol or deftype ceremony around plain data, nested let/doseq, or (not (empty? x)).
---

# Writing Idiomatic Clojure

## Recipe (all code generation)

1. **Model input and output as plain data** — maps, vectors, keywords; tag the
   kind of a thing with a key (`:report/kind`). Write the literal example data
   before the code.
2. **Transform one thing at a time** with core functions, composed. Pick the
   threader by argument position: `->` for functions taking the value first
   (`assoc`/`update`/interop), `->>` for seq ops (value last), `as->` when
   neither fits — see `references/threading.md`. No plural wrapper functions —
   callers compose `(map f xs)`.
3. **Open extension = multimethod on a value.** `(defmulti export (fn [item
   fmt] [(:kind item) fmt]))` with additive `defmethod`s. Two independent
   variation axes → two multimethods joined by a canonical intermediate data
   structure (N+M methods), never N×M pairs. Protocols only for Java
   interfaces or fixed polymorphism you own.
4. **Effects at the edges, eager, named with `!`.** Keep results → `mapv` or
   `(into [] xf xs)`; discard them → `run!` or `doseq`. Functions that *do*
   return eagerly collected results; never `doall` to force effects.

## Review requests

When reviewing or refactoring for idiom, read
`references/code-smells.md` and scan every row against the code. For threaded
code, also hand-expand every `->`/`->>`/`cond->` step —
`references/threading.md` lists the traps.

## House resolutions (don't relitigate)

- **Plural wrappers get deleted.** `(defn build-people [xs] (map
  build-person xs))` hardcodes "how many" into the API and can't be composed
  or transduced.
- **Nil-default fill:** `(merge-with (fn [d v] (if (nil? v) d v)) defaults m)`.
  Never `or` for this — it swallows `0` and `false`. If the output key set must
  be exactly `defaults`' keys, do it per-key explicitly and say why.
- **Summing:** `(reduce + 0 xs)`. `transduce` only where a transducer pipeline
  already exists. Never `(apply + xs)` on a large or lazy seq (varargs +
  stack).
- **Emptiness:** `(seq x)`, never `(not (empty? x))`. Negated filtering:
  `remove`, never `(filter #(not (p %)) ...)`.
- **Descending sort:** `(sort-by :k > xs)` only when ties are interchangeable.
  If ties pick a winner, don't rely on stability — put the tiebreaker in the
  sort key: `(sort-by (fn [m] [(- (:score m)) (:id m)]) xs)`.

## Java-in-Clojure red flags

- `doall` forcing effects; a lazy seq of effects crossing a boundary
- interface-shaped protocols or `deftype` ceremony around plain maps +
  `defmethod`
- `loop`/`recur` where `reduce`, `map`, or `for` exists
- `(doseq [x xs] (swap! acc conj ...))` accumulators
- hand-built key-by-key maps that want `merge` / `select-keys` / `cond->`
- `->>`/`->` on `merge`/`into`/`concat`-style calls — insertion position
  silently changes which argument wins
- asks phrased in OO-pattern terms (Builder, Observer, Visitor, …): see
  `references/patterns-in-clojure.md`.

Match surrounding style; rewrite only what your task covers.
