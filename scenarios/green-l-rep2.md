# GREEN L rep 2 (with skill) — PASS; all 4 baseline violations repaired + L round summary

Baseline: `(Band. …)` interop ctor, `deftest test-loud?`, no `testing`
blocks, `:deprecated` without `:superseded-by`. All four repaired, same as
rep1.

## Rubric

| item | baseline | result |
|---|---|---|
| record `Band` [name genre] | ✓ | ✓ (no docstring — see REFACTOR below) |
| validated creation | ✓ (anchor) | ✓ — `make-band`, explicit `IllegalArgumentException` with `(pr-str name)` in the message; cites "`(seq name)`, never `(not (empty? name))`" |
| construction idiom | ✗ `(Band. name genre)` | ✓ — `(->Band name genre)` |
| public `loud?` + private helper | ✓ | ✓ — `defn- loud-genre?`, set-as-predicate `(#{:metal :punk} genre)` (the code-smells idiom row) |
| deprecated `shout!` | PARTIAL | ✓ — `^{:deprecated true :superseded-by "loud?"}`, docstring stays purely behavioral |
| ns docstring | ✓ | ✓ — with backtick references to `make-band`/`loud?` |
| `deftest` naming | ✗ `test-loud?` | ✓ — `loud?-test` |
| `testing` blocks | ✗ none | ✓ — two, scenario-labeled |
| selective test require | — | ✓ — `[clojure.test :refer [deftest is testing]]` (rep1 used `:refer :all`; both fine) |
| independent re-run | — | ✓ — `{:test 1, :pass 3, :fail 0, :error 0}` (re-ran `clj -M -e '(test-ns …)'` in `scratch/music-bands-v2/`) |

```clojure
(ns music-bands.core
  "This library manages music bands: `make-band` creates a `Band` and
   `loud?` reports which bands are loud."
  (:require [clojure.string :as str]))

(defrecord Band [name genre])

(defn make-band
  "Returns a `Band` for `name` and `genre`.
   Throws `IllegalArgumentException` if `name` is not a non-empty string."
  [name genre]
  (when-not (and (string? name) (seq name))
    (throw (IllegalArgumentException.
           (str "`name` must be a non-empty string, got: " (pr-str name)))))
  (->Band name genre))

(defn- loud-genre?
  "True if `genre` is one of the loud genres: metal and punk."
  [genre]
  (#{:metal :punk} genre))

(defn loud?
  "True if `band` is loud: metal and punk bands are."
  [band]
  (loud-genre? (:genre band)))

(defn ^{:deprecated true :superseded-by "loud?"} shout!
  "Returns the name of `band` in upper case."
  [band]
  (str/upper-case (:name band)))
```

```clojure
(ns music-bands.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [music-bands.core :as music-bands]))

(deftest loud?-test
  (testing "metal and punk bands are loud"
    (is (music-bands/loud? (music-bands/make-band "Slayer" :metal)))
    (is (music-bands/loud? (music-bands/make-band "The Offspring" :punk))))
  (testing "other genres are quiet"
    (is (not (music-bands/loud? (music-bands/make-band "Adele" :pop))))))
```

Artifacts at `scratch/music-bands-v2/`, kept as evidence.

Notes: the rep declined the shared port-18888 nREPL ("left corrupted by
earlier runs") and verified in clean JVM processes instead — its claims
re-ran green for me, so the caution cost nothing. Its deps.edn uses the
cognitect test-runner with a proper `:exec-fn` alias (the correct 1.12
shape, vs rep1's broken `-m clojure.test` alias — see ledger row
`test-no-main`).

---

## L cross-rep convergence (both reps in)

| contested call | rep1 | rep2 |
|---|---|---|
| record construction | `map->Band {:name … :genre …}` | `->Band name genre` |
| validation shape | two separate `when-not` throws (distinct messages) | one combined `when-not (and …)` with `pr-str` in the message |
| loud-genre check | `(contains? #{:metal :punk} genre)` | `(#{:metal :punk} genre)` set-as-predicate |
| record docstring | tried, hit "No fields vector given.", dropped + disclosed | omitted silently |
| test require | `:refer :all` | selective `[deftest is testing]` |
| deftest / testing / deprecation meta | ✓ all three | ✓ all three |

All contested calls landed inside the skill's stated ranges; the two
validation shapes are both explicit `IllegalArgumentException` (the
anchor row), the two construction idioms are both generated ctors (the
repaired row), and both reps hit the deprecation-metadata row verbatim.

## L ROUND FINAL

| item | RED (baseline) | GREEN |
|---|---|---|
| generated ctor, not `Band.` | ✗ `(Band. …)` | **2/2** |
| `deftest` named `something-test` | ✗ `test-loud?` | **2/2** `loud?-test` |
| `testing` blocks | ✗ none | **2/2** |
| `:superseded-by` alongside `:deprecated` | ✗ missing | **2/2** |
| explicit-throw validation (anchor) | ✓ | 2/2 unchanged |
| tests re-run green | 3 assertions | 2/2 re-run independently |

**VERDICT: GREEN.** All four baseline violations repaired on both reps;
both anchors held.

## REFACTOR — `defrecord`/`deftype` docstring row shipped

The rep1/rep2 variance on the record docstring is the round's one real
signal: rep1 *tried* `(defrecord Band "doc" [name genre])`, hit
"No fields vector given.", and worked around it with a disclosure; rep2
omitted it silently. The trap is natural (the assumption was made by the
skill author too, this round) and the error message points at `fields`
instead of the docstring. Source-verified on 1.12.5: both macros are
`[name fields & opts+specs]` — no docstring slot, the string binds to
`fields`.

Shipped to `references/code-smells.md`, "Silent bugs and compile traps":

> `(defrecord Foo "doc" [a b])` / `(deftype Foo "doc" [a b])` — neither
> takes a docstring — the macro is `[name fields & opts+specs]`, so the
> string binds to `fields` and throws "No fields vector given.". Document
> the type in a `;;` comment above

Verification is in `scenarios/verified-snippets.md` (rows `defrecord
docstring`, reproduced for both macros). Retest decision: a full L
re-run would only re-prove that models avoid the trap they already do
(1/2 avoided, 1/2 hit-and-recovered); the row encodes a verified fact,
not a heuristic, so the ledger row + source check is the proportionate
retest. Noted as out-of-guide scope: this row comes from GREEN-round
evidence, not from guide.clojure.style (the guide's records section
covers construction only).
