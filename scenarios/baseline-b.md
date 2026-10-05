# Baseline B (no skill) — refactor smelly namespace — 10/11 rubric items fixed

Agent: general subagent, fresh session, no skill injected. 2026-10-03.
Notably, the agent self-verified by loading old+new in the shared nREPL and
diffing outputs on 19 cases (incl. nil/empty/200k-lines).

## Scorecard (fixture rubric)
| # | Smell | Result |
|---|---|---|
| 1 | doall+map for effects | FIXED → `run!` (disclosed return-value change) |
| 2 | filter #(not (zero? ...)) | FIXED → `(remove (comp zero? :total))` |
| 3 | apply concat + map | FIXED → `mapcat` |
| 4 | `build-reports` plural wrapper | **MISSED** — kept verbatim |
| 5 | loop/recur accumulation | FIXED → `transduce` + `+`, deduped with `line-total` |
| 6 | let-shadowing chain | FIXED → single let, `(sort-by :total >)` (no `->>` but shadowing gone) |
| 7 | (not (empty? x)) | FIXED → `(seq x)` |
| 8 | explicit do | FIXED |
| 9 | manual merge-defaults | FIXED → `merge-with` nil-preferring |
| 10 | nested doseq | FIXED → flattened bindings |
| 11 | apply + | FIXED → transduce |

Bonus finds not in rubric: juxt in totals-by-name; destructuring avoided
shadowing `first`/`last`; flagged latent `:items` bug; independently discovered
`remove-vals` absent from core 1.12.5 (matches my nREPL finding).

## Implications for skill
- Whole-catalog teaching NOT needed for generation — model knows 10/11 cold.
- Real gap candidate: **plural-wrapper rule** (bsless "Trivial Mapping Wrappers")
  — an opinionated structural rule models don't apply. Keep, with rationale.
- Skill value shifts to: (a) opinionated rules the model doesn't default to,
  (b) shared vocabulary + check-list when the USER asks "make it idiomatic"
  (review thoroughness shouldn't depend on model mood), (c) contested-call
  resolutions (transduce vs reduce vs ->>+reduce; merge-with vs prune+merge).
- GREEN must re-run B 2-3x: single clean run may be luck (variance is a metric).

## Verbatim rewritten namespace

```clojure
(ns report.core
  "Reporting over order rows."
  (:require [app.http :as http]
            [app.log :as log]
            [cheshire.core :as json]))

(defn- line-total
  "Price x quantity for a single order line."
  [{:keys [price qty]}]
  (* price qty))

(defn- sum-line-totals
  "Sum of the line totals; 0 for a report with no lines."
  [items]
  (transduce (map line-total) + 0 items))

(defn build-report
  "Shape a DB row into a report."
  [row]
  (let [{first-name :first last-name :last} row]
    {:id    (:id row)
     :name  (str first-name " " last-name)
     :total (sum-line-totals (:items row))}))

(defn build-reports
  "Lazy seq of reports, one per row."
  [rows]
  (map build-report rows))

(defn active-reports
  "Reports with a non-zero total."
  [reports]
  (remove (comp zero? :total) reports))

(defn all-lines
  "Every order line across all reports."
  [reports]
  (mapcat :items reports))

(defn notify!
  "POST one report to the notify endpoint."
  [report]
  (http/post! "/notify" {:body (json/write-str report)}))

(defn notify-all!
  "Notify every report, eagerly and in order."
  [reports]
  (run! notify! reports))

(defn totals-by-name
  "Totals keyed by report name. Later reports win when names repeat."
  [reports]
  (into {} (map (juxt :name :total)) reports))

(defn item-total
  "Sum of a report's line totals."
  [report]
  (sum-line-totals (:items report)))

(defn top-ten
  "Summary of the ten largest active reports, or nil when there are none."
  [reports]
  (let [top (into [] (take 10) (sort-by :total > (active-reports reports)))]
    (when (seq top)
      (log/info "top ten:" (count top))
      {:total   (transduce (map :total) + 0 top)
       :by-name (totals-by-name top)
       :count   (count top)})))

(defn merge-defaults
  "Report with every nil key filled in from defaults."
  [report defaults]
  (merge-with (fn [d r] (if (nil? r) d r)) defaults report))

(defn log-matrix
  "Log each x/y pair, outer loop over xs."
  [xs ys]
  (doseq [x xs
          y ys]
    (log/info x y)))
```

## Verbatim change list + caveats

- line-total / sum-line-totals (private) replace the inline #(* ...) and the
  hand-rolled loop/recur in item-total — the same calculation existed twice.
- apply + → transduce + explicit 0 (~2x on 200k lines; empty/nil explicit).
- filter #(not (zero? ...)) → remove (comp zero? :total); when (not (empty? x))
  → when (seq x).
- apply concat map → mapcat. doall+map → run!.
- totals-by-name uses juxt with the 3-arity into, no anonymous fn.
- top-ten: dropped four shadowed let bindings for one threaded pipeline,
  sort-by :total > instead of sort+reverse, into [] so result walked once,
  redundant (do ...) gone.
- merge-defaults: three copy-pasted if nil? clauses → one merge-with.
- log-matrix: nested doseq → one doseq with two bindings.
- Docstrings on every public var; helpers defn-; destructured :first/:last to
  avoid shadowing core/first and core/last.
- Heads-up: (merge defaults (remove-vals nil? report)) does not compile —
  remove-vals not in core 1.12.5. merge-with (or medley) is the way.
- Three behavior decisions flagged: build-report lacks :items (latent bug,
  left alone); notify-all! returns nil (offer mapv if responses consumed);
  merge-defaults now passes report-only keys through; duplicate names collapse.
- Suggestion not applied: top-ten logs, so it isn't pure despite the name.
