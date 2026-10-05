# GREEN B rep 2 (with skill) — PASS on the RED failure; converged on house rules

Key outcome vs baseline (which missed 2/2):
- **`build-reports` plural wrapper DELETED** ✅ (observed RED failure fixed)
- also deleted `notify-all!` with the same reasoning, callers compose `(run! notify! reports)`

## Scorecard vs baseline rubric (no regressions)
| # | Baseline smell | GREEN rep2 |
|---|---|---|
| 1 | doall+map | ✅ effects at edge via run! (notify-all! deleted) |
| 2 | filter #(not zero?) | ~ active-reports → `active?` predicate, callers `(filter active? …)` — smell removed via naming, `remove` not used (acceptable variant) |
| 3 | apply concat map | ✅ mapcat |
| 4 | plural wrapper | ✅ **DELETED (the fix)** |
| 5 | loop/recur | ✅ `reduce + 0`, dedupes build-report |
| 6 | let shadowing | ✅ ->> inside when-let |
| 7 | not empty? | ✅ `(when-let [top (seq …)])` |
| 8 | do | ✅ |
| 9 | manual merge | ✅ **verbatim house rule** `(merge-with (fn [d v] (if (nil? v) d v)) defaults report)` + the skill's escape clause ("key set must be locked → per-key + say why") |
| 10 | nested doseq | ✅ |
| 11 | apply + | ✅ `reduce + 0` (house default, NOT transduce — converged from baseline variance) |

Self-verified via project nREPL, incl. 0/false preservation through
merge-defaults.

## Watch items (for REFACTOR)
- Adopted `(sort-by :total >)` with **no tie-order disclosure** — baseline rep
  2 had flagged tie flip as observable. Skill silent on it. Candidate: one
  clause in house resolutions.
- Scope aggression: deleted public `notify-all!`, converted `active-reports`
  → `active?`. Task was "rewrite this namespace" so defensible, but callers
  unseen. Skill's last line ("licenses rewriting only what your task covers")
  didn't stop API deletions. Candidate: red-flag/house note — "plural-wrapper
  deletion: delete the wrapper, keep a !-named edge entry point if callers
  need one".

## Verbatim rewritten namespace

```clojure
(ns report.core
  (:require [app.http :as http]
            [app.log :as log]
            [cheshire.core :as json]))

(defn item-total [report]
  (reduce + 0 (map #(* (:price %) (:qty %)) (:items report))))

(defn build-report [row]
  {:id    (:id row)
   :name  (str (:first row) " " (:last row))
   :total (item-total row)})

(defn active? [report]
  (not (zero? (:total report))))

(defn all-lines [reports]
  (mapcat :items reports))

(defn notify! [report]
  (http/post! "/notify" {:body (json/write-str report)}))

(defn totals-by-name [reports]
  (into {} (map (juxt :name :total)) reports))

(defn top-ten [reports]
  (when-let [top (seq (->> reports
                           (filter active?)
                           (sort-by :total >)
                           (take 10)))]
    (log/info "top ten:" (count top))
    {:total   (reduce + 0 (map :total top))
     :by-name (totals-by-name top)
     :count   (count top)}))

(defn merge-defaults [report defaults]
  (merge-with (fn [d v] (if (nil? v) d v)) defaults report))

(defn log-matrix [xs ys]
  (doseq [x xs
          y ys]
    (log/info x y)))
```
