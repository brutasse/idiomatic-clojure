# Baseline F rep 1 — 4/4, CONTAMINATED (cwd discovery)

Wrote scratch/bandmanager/core.clj + scratch/bandmanager_test.clj into THIS
repo, read .nrepl-port ("you may want to restart it" — it found the dead
18889 port file mid-run). Used the skill's tie-order clause content
verbatim (sort-by > vs reverse fallback). Contamination via repo-as-cwd,
not skills-dir.

| # | Item | Result |
|---|---|---|
| F1 | top-bands predicate | ✅ `(every-pred :active? #(<= 3 (:members %)) (comp #{:rock :metal} :genre))` — composed, readable |
| F2 | boost pair | ✅ private generalized `boost [k factor bands]` + thin public wrappers — guide's double-op move |
| F3 | tagline builder | ✅ private singular `tagline`, `{n :name tags :tags}` destructuring |
| F4 | zip-taglines | ✅ `(fn [n ts] (tagline {:name n :tags ts}))` over two collections — no %n monster |

Also: caught the `{:keys [name]}` shadow trap independently ("hit this exact
ClassCastException while verifying"); noted keyword-stringify colon.

Verbatim code saved in this file's appendix.

## Appendix — verbatim answer
(ns bandmanager.core (:require [clojure.string :as str]))

(defn top-bands [bands]
  (->> bands
       (filter (every-pred :active?
                           #(<= 3 (:members %))
                           (comp #{:rock :metal} :genre)))
       (sort-by :score >)))

(defn- tagline [{n :name tags :tags}]
  (str (str/lower-case n) " :: " (str/join " · " (map name tags))))

(defn taglines [bands] (mapv tagline bands))

(defn zip-taglines [names tag-lists]
  (mapv (fn [n ts] (tagline {:name n :tags ts})) names tag-lists))

(defn- boost [k factor bands] (mapv #(update % k * factor) bands))
(defn boost-scores [factor bands] (boost :score factor bands))
(defn boost-members [factor bands] (boost :members factor bands))
