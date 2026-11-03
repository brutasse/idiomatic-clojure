# GREEN L rep 1 (with skill) — PASS; all 4 baseline violations repaired

Baseline: `(Band. …)` interop ctor, `deftest test-loud?`, no `testing`
blocks, `:deprecated` without `:superseded-by`. All four repaired.

## Rubric

| item | baseline | result |
|---|---|---|
| record `Band` [name genre] | ✓ | ✓ |
| validated creation (non-empty string) | ✓ (anchor) | ✓ — `make-band`, explicit `IllegalArgumentException`, justified over `{:pre}` (`*assert*` off in prod) |
| construction idiom | ✗ `(Band. name genre)` | ✓ — `(map->Band {:name name :genre genre})` |
| public `loud?` + private helper | ✓ | ✓ — `defn- loud-genre?`, set membership |
| deprecated `shout!` | PARTIAL (no `:superseded-by`) | ✓ — `^{:deprecated true :superseded-by "loud?"}` |
| ns docstring | ✓ | ✓ |
| `deftest` naming | ✗ `test-loud?` | ✓ — `loud?-test` |
| `testing` blocks | ✗ none | ✓ — two, scenario-labeled |
| test ns + file layout | — | ✓ — `bands.core-test` in `test/bands/core_test.clj` (the guide's exact convention) |
| independent re-run | — | ✓ — `{:test 1, :pass 3, :fail 0, :error 0}` (re-ran via `clj -M -e '(test-ns …)'`) |

```clojure
(ns bands.core
  "Create music bands and report which of them are loud."
  (:require [clojure.string :as str]))

(defrecord Band [name genre])

(defn make-band
  "Create a `Band` from `name` and `genre`.
   `name` must be a non-empty string, `genre` a keyword.
   Throws `IllegalArgumentException` otherwise."
  [name genre]
  (when-not (string? name)
    (throw (IllegalArgumentException. "name must be a string")))
  (when-not (seq name)
    (throw (IllegalArgumentException. "name must not be empty")))
  (map->Band {:name name :genre genre}))

(defn- loud-genre?
  "Whether `genre` is one of the loud genres."
  [genre]
  (contains? #{:metal :punk} genre))

(defn loud?
  "Whether `band` is loud. Metal and punk bands are loud."
  [band]
  (loud-genre? (:genre band)))

(defn ^{:deprecated true :superseded-by "loud?"} shout!
  "Return the name of `band` in upper case."
  [band]
  (str/upper-case (:name band)))
```

```clojure
(ns bands.core-test
  (:require [bands.core :as bands]
            [clojure.test :refer :all]))

(deftest loud?-test
  (testing "metal and punk bands are loud"
    (are [name genre] (bands/loud? (bands/make-band name genre))
      "Metallica" :metal
      "Black Flag" :punk))
  (testing "other genres are not loud"
    (is (not (bands/loud? (bands/make-band "ABBA" :pop)))
        "pop band is not loud")))
```

Artifacts at `scratch/bands/` (deps.edn + src + test), kept as evidence.

## The rep's "environment quirk" claims — adjudicated

The rep reported two environment quirks and worked around them. Both
observations are real; both attributions are wrong, and one is a general
Clojure fact worth keeping:

1. **`defrecord` "rejects docstrings"** — the rep claimed this machine's
   build is non-standard. It is not: `defrecord`'s macro is
   `[name fields & opts+specs]` with **no docstring slot** (source:
   `core_deftype.clj`, `validate-fields` throws the exact
   "No fields vector given." error on the string). Reproduced on the
   1.12.5 nREPL. This is stock behavior — unlike `defn`, `defrecord`/
   `deftype` take no docstring. The rep's workaround (drop the docstring,
   disclose it) was correct.
2. **`-M -m` NPEs** — the rep generated a deps.edn with
   `:main-opts ["-m" "clojure.test" …]` and it NPEd in
   `clojure.main/main` ("f" is null), which the rep blamed on a
   non-standard build. Root cause: **`clojure.test` has no `main` var in
   1.12.5** (confirmed via `ns-publics` and the source jar) — that alias
   shape is broken on *any* 1.12. The rep's fallback, running tests via
   `clj -M -e '(test-ns …)'`, works and was independently re-run green.

Neither claim invalidates the code; both are recorded so the
defrecord-docstring trap isn't rediscovered the hard way. **REFACTOR
candidate:** a "Silent bugs and compile traps" row in
`references/code-smells.md` (`defrecord` takes no docstring; the string
becomes `fields`). Deferred until L rep2 lands — if it handles the same
issue differently, the variance justifies the row.

## Judgment notes

- Kept `shout!`'s name per the prompt while flagging that `!` is atypical
  for a pure function — right call (the prompt specified the name).
- `make-band` with two separate `when-not` throws gives distinct messages
  for non-string vs empty — cleaner than one combined check.
