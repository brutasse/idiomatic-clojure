# Baseline G rep 2 (skill UNLINKED — clean) — 4/6; G3 delta item MISSED

## Scorecard (rubric: scoring-hof.md)
| # | Item | Result |
|---|---|---|
| G1 | loop/recur filter | ✅ `filterv` (kept vector return type deliberately) |
| G2 | inline `= :dog` predicate | **PARTIAL** — moved out of loop but stayed anonymous: `#(= :dog (:species %))`; no `dog?` name, no `(comp #{:dog} :species)`. (Named-predicate PRINCIPLE clearly applied elsewhere — `streamable?` — so the principle is known; just not for the trivial one-liner) |
| G3 | double-sum/double-product pair | ❌ **MISSED** — polished internals (`#(* 2 %)` → `(partial * 2)`, dropped redundant reduce inits) but pair stays copy-paste-shaped; duplication never mentioned. No generalization to op-as-argument |
| G4 | taglines nested literal | ✅ extracted singular `tagline` helper + `format` template — textbook; ALSO caught the `{:keys [name]}` destructuring shadowing `clojure.core/name` footgun |
| G5 | streamable 3-clause inline predicate | ✅ named `streamable?`, set-as-predicate, destructured condition |
| G6 | over-correction | ✅ no ceremony, API preserved |

Claims to verify in nREPL batch: `(reduce + [])` → 0, `(reduce * [])` → 1
(init-drop correctness).

## Notes
G3 is the first genuine MISS on a guide-delta item in any clean baseline.
G2 partial: trivial one-line predicates stay anonymous — arguably fine
(readability ceiling cuts both ways). Wait for G rep 1: if it generalizes
the pair, G3 is variance, not gap.

## Verbatim answer
(ns app.bands
  (:require [clojure.string :as str]))

(defn keep-dogs [animals]
  (filterv #(= :dog (:species %)) animals))

(defn double-sum [nums]
  (reduce + (map (partial * 2) nums)))

(defn double-product [nums]
  (reduce * (map (partial * 2) nums)))

(defn tagline [{:keys [tags] :as band}]
  (format "%s :: %s"
          (str/lower-case (:name band))
          (str/join " · " (map name tags))))

(defn taglines [bands]
  (map tagline bands))

(defn streamable? [{:keys [active? members genre]}]
  (and active?
       (>= members 3)
       (#{:rock :metal} genre)))

(defn streamable [bands]
  (filter streamable? bands))

Rationale highlights: filterv keeps vector/lazy-free with filter-identical
behavior; reduce +/∗ self-initializing (verified); split singular tagline
for test/reuse; format shows tagline shape in template; deliberate non-
destructuring of :name to avoid shadowing core/name; streamable? reusable
with some/remove; equivalence harness over old vs new on edge cases.
