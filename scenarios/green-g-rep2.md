# GREEN G rep 2 (updated skill) — PASS; new rows demonstrably USED

| # | Item | Result |
|---|---|---|
| G1 | loop/recur | ✅ `filter` + `(comp #{:dog} :species)`; flagged lazy-seq-vs-vector change |
| G3 | double pair | ✅ **generalized**: private `(reduce-doubled [op nums])` + public pair via `partial` — "a defn pair differing only in an embedded operation is a duplication smell" quotes the new row; dropped inits with the verified reason ("+ and * supply their own, (reduce + []) → 0") — the exact fact rep 1 got wrong in RED |
| G4 | taglines literal | ✅ singular `tagline` extracted, plural deleted per house rule, API change disclosed with re-add-wrapper caveat; deliberately avoided `{:keys [name]}` shadow |
| G5 | streamable predicate | ✅ named `streamable?`, "a predicate is domain policy: testable, reusable, greppable" — **the new row's Why column near-word-for-word** |
| G6 | over-correction | ✅ explicit restraint: kept named collection queries ("filters encode policy, not 'how many'"), no gratuitous ->>/transducers |

Verbatim code:
(ns app.bands (:require [clojure.string :as str]))

(defn keep-dogs [animals] (filter (comp #{:dog} :species) animals))
(defn- reduce-doubled [op nums] (reduce op (map #(* 2 %) nums)))
(def double-sum (partial reduce-doubled +))
(def double-product (partial reduce-doubled *))

(defn tagline [band]
  (str (str/lower-case (:name band)) " :: " (str/join " · " (map name (:tags band)))))

(defn streamable? [{:keys [active? members genre]}]
  (and active? (>= members 3) (contains? #{:rock :metal} genre)))
(defn streamable [bands] (filter streamable? bands))

Claim check: "9 equivalence checks incl. double-product [] → 1" — consistent
with our own verified `(reduce * [])` → 1.
RED→GREEN flip on G3: 0/2 → 1/1 so far (rep 1 pending).
