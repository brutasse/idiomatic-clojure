# Baseline L (no skill) — library namespace — 4 observed violations, all in conventions territory

Agent: general subagent, fresh session, no skill injected. 1 rep
(`ses_e6300bb3effekkAew82ZYmbppp`), 2026-11-02. Prompt:
`prompt-l-library.md` (record `Band`, validated creation, public `loud?` +
private metal/punk helper, deprecated `shout!`, ns docstring, one `deftest`
for `loud?`). The agent built and ran a full `scratch/music-bands/` project
(deps.edn, src, test, cognitect test-runner) — 1 test, 3 assertions, 0
failures.

## Scorecard

| rubric item | result |
|---|---|
| record `Band` with name/genre | PASS |
| validated creation (non-empty string) | PASS — `make-band` throws explicit `IllegalArgumentException`; justified rejecting `assert`/`{:pre}` because `*assert*` is off in production |
| public `loud?` + private helper | PASS — `defn- loud-genre?`, set membership |
| deprecated `shout!` | PARTIAL — `^:deprecated` + docstring, but no `:superseded-by` metadata |
| ns docstring | PASS |
| `deftest` with 2+ cases | PARTIAL — exists and passes, but named `test-loud?` (the exact bad pattern the guide lists) with no `testing` blocks |
| record construction | **FAIL** — `(Band. name genre)` interop constructor instead of `->Band` |

## Verbatim output (core namespace, abridged to the agent's own listing)

```clojure
(ns music-bands.core
  "Create music bands and report which ones are loud."
  (:require [clojure.string :as str]))

(defrecord Band [name genre])

(defn make-band
  "Create a band. name must be a non-empty string, genre a keyword."
  [name genre]
  (if (and (string? name) (not (empty? name)))
    (Band. name genre)
    (throw (IllegalArgumentException. "band name must be a non-empty string"))))

(defn- loud-genre?
  "True if genre is metal or punk."
  [genre]
  (contains? #{:metal :punk} genre))

(defn loud?
  "True if band plays a loud genre (metal or punk)."
  [band]
  (loud-genre? (:genre band)))

(defn ^:deprecated shout!
  "Deprecated: use loud? instead.
   Returns the band's name in upper case."
  [band]
  (str/upper-case (:name band)))
```

```clojure
(ns music-bands.core-test
  (:require [clojure.test :refer :all]
            [music-bands.core :refer [loud? make-band]]))

(deftest test-loud?
  (is (loud? (make-band "Slayer" :metal)))
  (is (loud? (make-band "Bolt Thrower" :punk)))
  (is (not (loud? (make-band "Adele" :pop)))))
```

Artifacts kept at `scratch/music-bands/` (deps.edn + src + test) as
evidence; ran green under `clj -M:test`.

## Note on an unfounded claim

The agent reported a "JVM/Clojure gotcha": *file paths use underscores — the
JVM loader only resolves munged resource names, so `music-bands.core` must
live at `src/music_bands/core.clj`, not `src/music-bands/`*. This is not
supported: hyphenated directories are the standard for hyphenated namespace
segments (munging affects generated class names like
`music_bands.core$Band`, not source-file lookup). The agent's renamed
layout works; the original would too. Recorded so it isn't repeated as
fact.

## Implications

- The L scenario is the strongest conventions-drafting signal: `->Band`,
  `deftest` naming (`something-test`, never `test-something`), `testing`
  blocks, and `:superseded-by` are all conventions rows — each one observed
  missing in the baseline.
- The validation row (`make-*` with explicit throw, justified over `{:pre}`
  because `*assert*` is off) is an anchor: the baseline did it well
  unprompted. The skill should state it so it stays well-done.
- `defn-`, idiomatic `str` alias, and ns docstring were correct — anchors.
