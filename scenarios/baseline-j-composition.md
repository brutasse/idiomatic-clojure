# Baseline J (no skill) — call-site composition — RED confirmed 3/5 used `comp`

Agent: general subagent, fresh session, no skill injected, "answer from your
own knowledge only". 5 reps, 2026-11-02. Prompt: `prompt-j-composition.md`
(two tasks: `loud-active` — sequence of uppercased display names for the
active bands using `active?`/`display-name`; `odd-inc` — named transducer
keeping odds then incrementing).

## Scorecard

| rep | `loud-active` (call site) | `odd-inc` (named transducer) | verdict |
|---|---|---|---|
| 1 | neutral: `->>` with two plain `map` steps | `(comp (filter odd?) (map inc))` ✓ | PASS |
| 2 | **`comp`**: `(map (comp clojure.string/upper display-name) …)` | comp ✓ | **FAIL** |
| 3 | fn literal: `#(clojure.string/upper-case (display-name %))` | comp ✓ | PASS |
| 4 | **`comp`**: `(map (comp clojure.string/upper-case display-name))` in `->>` | comp ✓ | **FAIL** |
| 5 | **`comp`**: `(map (comp #(.toUpperCase %) display-name))` in `->>` | comp ✓ | **FAIL** |

Call-site tally: 3/5 `comp`, 1/5 fn literal, 1/5 neutral. `odd-inc`: 5/5
`comp` — the correct shape for a named transducer, so the rule must carve it
out, not ban `comp`.

## Verbatim outputs

Rep 1 (`ses_e6301a1cdffes74mwoTNnMjXAw`):

```clojure
(defn loud-active
  "Uppercased display names of the active bands."
  []
  (->> bands
       (filter active?)
       (map display-name)
       (map clojure.string/upper-case)))

(def odd-inc
  (comp (filter odd?) (map inc)))
```

Rep 2 (`ses_e6301a1cbffere3uo1aJTWVDgq`):

```clojure
(defn loud-active
  "Upper-cased display names of the active bands."
  []
  (map (comp clojure.string/upper display-name)
       (filter active? bands)))

(def odd-inc
  (comp (filter odd?) (map inc)))
```

Rep 3 (`ses_e6301a1d4ffe24e0HNbKYPUUL9`):

```clojure
(defn loud-active
  "Upper-cased display names of the active bands."
  [bands]
  (->> bands
       (filter active?)
       (map #(clojure.string/upper-case (display-name %)))))

(def odd-inc
  (comp (filter odd?) (map inc)))
```

Rep 4 (`ses_e6301a1d1ffep5C3M8OyAOhsfB`):

```clojure
(defn loud-active
  []
  (->> bands
       (filter active?)
       (map (comp clojure.string/upper-case display-name))))

(def odd-inc
  (comp (filter odd?) (map inc)))
```

Rep 5 (`ses_e6301a1c7ffe9jbAlBSoMPUY0c`):

```clojure
(defn loud-active
  "Uppercased display names of the active bands."
  []
  (->> bands
       (filter active?)
       (map (comp #(.toUpperCase %) display-name))))

(def odd-inc
  "Transducer keeping only odd numbers, then incrementing each."
  (comp (filter odd?) (map inc)))
```

## Fixture note (no skill impact)

`display-name`'s docstring claims output `"Kowelski [rock]"`, but on
Clojure 1.12 `str` keeps the keyword colon, so the real output is
`"Kowelski [:rock]"`. Reps 3 and 5 spotted and disclosed it; the others
silently "verified" against the docstring.

## Implications

- RED for the composition house resolution is real: a bare model reaches for
  `comp` at the call site about half the time, and never reaches for a fn
  literal unprompted.
- The rule needs two halves: fn literal at the call site (reads in
  execution order; `comp` applies right-to-left and inverts it) AND `comp`
  reserved for named transducer pipelines (all 5 reps got `odd-inc` right —
  don't regress that).
- `partial`/`complement` were not observed here but the guide's rule covers
  them; the house resolution should cover all three so GREEN has one rule.
