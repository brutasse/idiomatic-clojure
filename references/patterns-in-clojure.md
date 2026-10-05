# Design patterns, dissolved

Use when an ask arrives phrased in OO-pattern terms. Map the pattern to its
Clojure core construct instead of reproducing the class diagram. (Source:
mishadoff, "Clojure Design Patterns".)

| Pattern | In Clojure it is just… |
|---|---|
| Command | a function (or `(partial f args)` for a delayed call; save `[f args]`, `(apply f args)`) |
| Strategy | a function passed to a higher-order function — `sort-by`, `comparator`, `update` |
| State | multimethod on a state key; a transition is a pure fn `state → state` |
| Template Method | multimethod on `(:kind x)`, or an options map of fns with defaults |
| Visitor | multimethod on two values: `(fn [item fmt] [(:kind item) fmt])` |
| Iterator | `seq`; infinite/lazy via `iterate`/`lazy-seq`; custom types implement `seq` |
| Memento | the immutable value *is* the snapshot; keep a history vector in an atom |
| Prototype | unnecessary — persistent data + `merge` of a defaults map |
| Mediator | a function over data; passing values *is* the decoupling |
| Observer | `add-watch` on a reference type (watches see every change, even ones made around your mutators); core.async `pub/sub` for the distributed case |
| Interpreter | multimethod on `class` (or a tag key) walking a data tree — code is data |
| Flyweight | `memoize` a pure factory; or one shared constant data structure |
| Builder | one map argument + `cond->` + `{:pre [...]}` for required keys |
| Facade | a thin public function over a namespace's internals |
| Singleton | `def` — a var is already a singleton value |
| Chain of Responsibility | `some`, `cond`, or `some->`; a transducer pipeline |
| Composite | nested collections + recursion (`tree-seq`, `walk`) |
| Factory / Abstract Factory | plain function returning a map, often a multimethod on a type tag |
| Adapter / Decorator / Proxy | `comp` and closures; `reify`/`proxy` **only** when a Java interface demands it |
| Bridge | two multimethods over a shared intermediate representation (see the N+M rule in SKILL.md) |

**Legitimate ceremony, keep it:** implementing a Java interface (`reify`,
`proxy`, `deftype`), performance-mandated mutable arrays/`transient`, and
protocols as documented boundaries you own. Everything above that is usually
a map and a `defmethod`.

When extending an existing design across **two** independent axes (report
kind × export format), don't add a multimethod per pair — normalize each kind
to a canonical intermediate map and dispatch formats over that: N+M methods.
