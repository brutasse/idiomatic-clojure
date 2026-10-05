# Baseline F rep 2 — 3/4; F1 FAIL; CONTAMINATED ("this repo's style")

Quoted the skill's plural-wrapper rule near-verbatim and said "wrappers this
repo's style deletes on sight" — read SKILL.md from cwd with the symlink
unlinked. Contamination confirmed again.

| # | Item | Result |
|---|---|---|
| F1 | top-bands predicate | ❌ 3-clause anonymous predicate inline: `(filter #(and (:active? %) (<= 3 (:members %)) (#{:rock :metal} (:genre %))) ...)` — buried in filter |
| F2 | boost pair | ✅ strongest form: singular `boost [k factor band]` + caller-side `(mapv #(boost :score 1.1) bands)` — but this is the SKILL rule, not native |
| F3 | tagline builder | ✅ singular `tagline` helper, destructured |
| F4 | zip-taglines | ✅ `(fn [n ts] (tagline {:name n :tags ts}))` |

F1 fail is notable given contamination: the agent HAD the skill open (quoted
it) and still inlined the predicate → binding check on existing code-smells
coverage (does the catalog have an every-pred/predicate-naming row? check) —
if yes: REFACTOR problem (row exists, doesn't bind); if no: new row.

## Appendix — verbatim core
(defn top-bands [bands]
  (->> bands
       (filter #(and (:active? %) (<= 3 (:members %)) (#{:rock :metal} (:genre %))))
       (sort-by :score >)))

(defn tagline [{title :name tags :tags}]
  (str (str/lower-case title) " :: " (str/join " · " (map name tags))))

(defn zip-taglines [names tag-lists]
  (map (fn [n ts] (tagline {:name n :tags ts})) names tag-lists))

(defn boost [k factor band] (update band k * factor))
;; callers: (map tagline bands), (mapv #(boost :score 1.1) bands)
