# Clojure conventions: names, defs, and per-construct rules

Companion to `code-smells.md` (the rewrite catalog): how to name things,
how to declare them, and the rules around constructs the catalog doesn't
cover. Read it when generating or reviewing a whole namespace, and for the
constructs it lists. All examples run on Clojure 1.12.

Whitespace and layout — line length, indentation, alignment, line endings,
commas, blank lines, trailing parens, docstring indentation — are the
formatter's (cljfmt's) job and are deliberately absent here.

## Names

| Rule | Example |
|---|---|
| `lisp-case` for functions and variables — no underscores, no camelCase | `some-var`, `some-fun` |
| `CapitalCase` for protocols, records, `deftype`s; keep acronyms uppercase | `defrecord HttpRequest`, `deftype XMLParser` |
| `?` on predicates | `loud?`, `palindrome?` — never `is-loud` / `loud-p` |
| `!` on side-effecting or transaction-unsafe functions | `save-user!`, `reset!` — signals "don't call this inside `dosync`" |
| `->` for conversion functions | `f->c`, not `f-to-c` |
| earmuffs for dynamic vars | `(def ^:dynamic *a* 10)` |
| no constant notation — everything is constant unless rebound | `(def max-size 10)` — not `MAX_SIZE` / `+max-size+` |
| `_` for ignored values; `_name` when naming them helps | `(let [[a b _] [1 2 3]] …)` |
| idiomatic single letters: `f`/`g`/`h` (fn), `n` (size), `i` (index), `x`/`y`, `xs` (seq), `m` (map), `k`/`v`, `s` (string), `re`, `coll`, `pred`, `xf` (transducer) | |

## Namespaces

- No single-segment namespaces (`(ns example.ns)`, not `(ns example)`); at
  most ~5 segments. `project.module` or `org.project.module`; a single-ns
  library is `project.core`.
- One file per namespace; one namespace per file.
- Comprehensive `ns` form; sorted `:require`s; `:require [x :as y]` over
  `:refer` over `:refer :all`; never `:use`.
- No `require`/`refer` calls in source files — that's REPL furniture.

Idiomatic aliases (use these, and the same alias everywhere in the project):

| namespace | alias |
|---|---|
| `clojure.string` | `str` |
| `clojure.math` | `math` |
| `clojure.set` | `set` |
| `clojure.walk` | `walk` |
| `clojure.zip` | `zip` |
| `clojure.edn` | `edn` |
| `clojure.datafy` | `datafy` |
| `clojure.java.io` | `io` |
| `clojure.java.shell` | `sh` |
| `clojure.pprint` | `pp` |
| `clojure.spec.alpha` | `s` |
| `clojure.tools.logging` | `log` |
| `cheshire.core` | `json` |
| `clj-http.client` | `http` |
| `next.jdbc` | `jdbc` |
| `java-time` | `time` |

Recipe for everything else: (1) alias = the trailing segment(s) of the
namespace; (2) keep enough trailing segments to stay unique — dots are
allowed in aliases (`[clojure.xml :as xml]`, `[clojure.data.xml :as
data.xml]`); (3) drop redundant words like `core`/`clj`.

## Defs and docstrings

- The docstring goes **after the name, before the arg vector**. After the
  arg vector it is silently the first body form: `:doc` is nil, and a
  one-form function returns the string instead of computing (verified).
  Exception: `defprotocol` methods take their docstring after the arg
  vector.
- First line: a complete, capitalized sentence — tooling shows it as the
  summary. Document positional arguments and var references in backticks;
  markdown is supported.
- `defn-` for private functions; `^:private` for vars. Don't write
  `^:private` on `defn` — `defn-` already does it. Compact notation for
  true flags: `^:private`, not `^{:private true}`.
- Metadata attaches to the **var**, not the value: `(def ^:private a {})`
  puts it in `(meta #'a)`, not `(meta a)`.
- Access a private var (e.g. from tests) with `@#'ns/var`.
- API metadata, string values, omit the patch version: `:added "0.5"`,
  `:changed`, `:deprecated` (version, or `true` unversioned),
  `:superseded-by "new-fn"` (combine with `:deprecated`), `:see-also
  ["other-fn"]`, `:no-doc`. Deprecate with metadata, never with a
  "DEPRECATED:" line in the docstring.
- Validation: `{:pre [...]}` for internal functions (throws
  `AssertionError`, controlled by `*assert*`). Public library APIs that
  must reject input even with `*assert*` off: an explicit check that
  throws a standard exception type.
- No `def` inside function bodies. Rebind an existing var with
  `alter-var-root`. Don't shadow `clojure.core` names with arguments or
  locals.
- Avoid forward references; when one is truly required, `declare` it.
- Keep functions short (most under 5 lines, rarely over 10); order
  arities from fewest to most arguments.

## Records

- Construct with the generated constructors: `->Band`, `map->Band` —
  never the interop `Band.`. (`map->` exists for records only; `deftype`
  gets no `map->`.)
- Don't redefine the auto-generated `->Foo`/`map->Foo` — readers expect
  their behavior. For validated creation, add a `make-foo` wrapper with
  the check (or `{:pre}`) inside it.

## Interop

- Sugared forms: `(java.util.ArrayList. 100)`, `(Math/pow 2 10)`,
  `(.substring s 1 3)`, `Integer/MAX_VALUE`, `(.field obj)` — not
  `new`/`(. Class meth args)`.
- `clojure.string/*` over raw string interop; `clojure.math/*` over
  `java.lang.Math` interop (1.11+: primitive overloads, `long`/`double`).
- `with-open` over `try`/`finally` for closing resources.
- Avoid Java collections and arrays outside interop boundaries; convert at
  the edge (this also fixes `hash` mismatches — see `code-smells.md`).

## Exceptions

- Throw standard types, matched to the situation:
  `IllegalArgumentException` (bad argument), `IllegalStateException` (bad
  state), `UnsupportedOperationException`, `IOException`. Carry data with
  `ex-info`. No custom exception types for ordinary errors.
- Don't catch `Throwable` — it swallows `Error`s. Catch the specific
  types you expect.
- `(import clojure.lang.ExceptionInfo)` in the `ns` form before you can
  `catch ExceptionInfo` by name (verified: otherwise a compile error).

## STM

- Keep transactions small; do I/O and blocking work outside `dosync`.
  Guard I/O functions with `io!` — it throws if called inside a
  transaction, so accidental in-tx I/O fails loudly instead of running
  (possibly several times) silently (verified).
- Never update an atom inside a transaction: `swap!` inside `dosync` runs
  once per **attempt**, so a retried transaction appends duplicate entries
  while the ref change applies once (verified: deterministic retry → 2 log
  entries, 1 committed change). Do it after the `dosync`.
- `alter` over `ref-set` where possible.
- Reads inside a transaction are plain `deref`/`@`. There is no
  `ref-deref` (verified absent from 1.12).
- `ensure ref` is touch semantics: it marks the ref read by the
  transaction and returns its in-transaction value — it takes a **Ref**,
  not a boolean.
- Avoid mixing long- and short-running transactions on the same ref —
  every short commit retries the long one from scratch.
- Agents: `send` for CPU-bound actions (fixed pool — blocking starves
  other agents), `send-off` for anything that may block.

## Macros

- A function beats a macro unless evaluation order, arity, or syntax
  genuinely require a macro.
- Design from the call site: write the desired usage first, then the
  macro.
- A macro should be thin sugar over a plain function — the logic lives in
  the function, where it can be tested and called.
- Syntax-quote (backtick) over manual `list`/`cons` building.

## Comments

- `;;;;` section heading; `;;;` top-level (between top-level forms); `;;`
  a line comment above the code it describes; `;` margin comment.
- Space after the semicolon. Multi-word comments start capitalized and end
  with punctuation.
- No comments that restate the code; keep comments current — an outdated
  comment is worse than none. `#_` to disable a single form. Refactor
  instead of explaining bad code.
- Annotations go on the line **above** the code: `;; FIXME: …`,
  `;; TODO: …`, `;; OPTIMIZE: …`, `;; HACK: …`, `;; REVIEW: …` — keyword,
  colon, space, note.

## Testing

- Tests live in `test/`, mirroring `src/`; the test namespace of
  `something` is `something-test`.
- Name `deftest`s `something-test` — not `test-something`, not
  `something-tests`, not bare `something`.
- Group related assertions under `testing` blocks so failures say which
  scenario broke; give `is` a message when the assertion alone doesn't
  explain the failure.
- `are` for the same logic over many inputs; one concept per `deftest`.
- `with-redefs` sparingly (external boundaries only); prefer taking
  dependencies as arguments.
- Expected exceptions are `is` forms — `thrown?` and `thrown-with-msg?`
  are `assert-expr` dispatches, not referable vars, so they only work
  inside `is` (verified: `:refer [thrown?]` fails to compile):
  `(is (thrown? ArithmeticException (/ 1 0)))`,
  `(is (thrown-with-msg? ExceptionInfo #"Invalid" (validate! nil)))`.
- `matcher-combinators` (`match?`, `thrown-match?`) for structural
  assertions if the project already depends on it.

## Library organization

- Coordinates per Central Repository guidelines, or the `org/name`
  convention (`nrepl/nrepl`, `clj-commons/fs`).
- Minimize dependencies — a three-line copy beats a jar you'll barely use.
- Ship core functionality and tooling integrations as separate artifacts.
