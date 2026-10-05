# Baseline B rep 2 (no skill) — 10/11 again, plural wrapper missed again (2/2)

Agent: second fresh general subagent, same prompt. 2026-10-03.
Also self-verified via nREPL diff, 19/19 cases.

## Scorecard
| # | Smell | rep1 | rep2 |
|---|---|---|---|
| 1 | doall+map → run! | ✅ | ✅ |
| 2 | filter/not → remove+comp | ✅ | ✅ |
| 3 | apply concat → mapcat | ✅ | ✅ |
| 4 | plural wrapper build-reports | ❌ | ❌ |
| 5 | loop/recur | ✅ transduce | ✅ reduce+dedup helper |
| 6 | let-shadowing | ✅ single let | ✅ ->> pipeline (variance, both valid) |
| 7 | not empty? → seq | ✅ | ✅ |
| 8 | explicit do | ✅ | ✅ |
| 9 | manual merge-defaults | ✅ merge-with | ~ half: named `non-nil-or` helper, 3 manual keys kept |
| 10 | nested doseq | ✅ | ✅ |
| 11 | apply + | ✅ transduce | ✅ reduce + 0 |

## Variance findings
- ALL-RUN FAILURE: plural wrapper (2/2 missed) → first-class skill content.
- CONTESTED resolution: merge-defaults — rep1 `(merge-with nil-pref defaults
  report)` (report-only keys now pass through = behavior change), rep2 manual
  3-key `non-nil-or` (exact key set preserved). rep2 also flagged the `or` trap
  (legit 0 total falses the default). → skill should RESOLVE this one way.
- VARIANCE: transduce vs reduce vs ->>+reduce — model alternates; skill picks
  the house default (plain/reduce for small, transduce/into-xf for big).
- rep2 flagged `sort-by :total >` tie-order change — good catch, shows
  baseline quality is high; skill must not add noise on top of this.

## Verbatim rewritten namespace (rep 2)

```clojure
(ns report.core
  (:require [app.http :as http]
            [app.log :as log]
            [cheshire.core :as json]))

(defn- line-total [{:keys [price qty]}]
  (* price qty))

(defn- sum-line-totals [items]
  (reduce + 0 (map line-total items)))

(defn- non-nil-or
  "V, or FALLBACK when V is nil. Unlike `or`, this keeps 0 and false."
  [v fallback]
  (if (nil? v) fallback v))

(defn build-report [row]
  {:id    (:id row)
   :name  (str (:first row) " " (:last row))
   :total (sum-line-totals (:items row))})

(defn build-reports [rows]
  (map build-report rows))

(defn active-reports [reports]
  (remove (comp zero? :total) reports))

(defn all-lines [reports]
  (mapcat :items reports))

(defn notify! [report]
  (http/post! "/notify" {:body (json/write-str report)}))

(defn notify-all! [reports]
  (run! notify! reports))

(defn totals-by-name [reports]
  (into {} (map (juxt :name :total)) reports))

(defn item-total [report]
  (sum-line-totals (:items report)))

(defn top-ten
  "Summary of the ten highest-total active reports, or nil when there are none."
  [reports]
  (let [top (->> reports active-reports (sort-by :total) reverse (take 10))]
    (when (seq top)
      (let [n (count top)]
        (log/info "top ten:" n)
        {:total   (transduce (map :total) + top)
         :by-name (totals-by-name top)
         :count   n}))))

(defn merge-defaults
  "Replace nil fields of `report` with those of `defaults`."
  [report defaults]
  {:id    (non-nil-or (:id report) (:id defaults))
   :name  (non-nil-or (:name report) (:name defaults))
   :total (non-nil-or (:total report) (:total defaults))})

(defn log-matrix [xs ys]
  (doseq [x xs y ys]
    (log/info x y)))
```

Verbatim notes: de-duped money math (line-total/sum-line-totals; original
loop/recur had an auto-boxing recur warning); apply + → reduce + 0; remove +
comp; mapcat; juxt as transducer; top-ten threaded, seq, no do, count once;
merge-defaults named helper, deliberately not `or` (0-total trap) nor merge/get
(missing vs present-nil); doseq flattened; docstrings where name doesn't carry
meaning; laziness preserved deliberately. Sign-off item: notify-all! returns
nil. Rejected: sort-by > tie-order flip; partial selection left alone.
Noticed: "nil nil" names, duplicate-name collapse.
