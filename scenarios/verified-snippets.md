# Verified snippets (nREPL, Clojure 1.12.5, port 18888)

All evaluated true/pass unless noted. Any snippet that reaches the skill must
appear here.

| id | snippet | status |
|---|---|---|
| mapcat | `(mapcat :items [{:items [1 2]} {:items [3]}])` → `[1 2 3]` | PASS |
| remove+comp | `(remove (comp zero? :total) [{:total 0} {:total 5}])` | PASS |
| sort-by juxt | `(sort-by (juxt (complement :subscription) :name) people)` | PASS |
| group-by / set/index | `(contains? (group-by :person/name people) "Fred")`, `(clojure.set/index people [:person/name])` | PASS |
| seq vs not-empty | `(if (seq x) ...)` on vector/[]/nil | PASS |
| into + xform | `(into [0] (map inc) [1 2 3])` → `[0 2 3 4]` | PASS |
| prune nils (core) | `(reduce-kv (fn [m k v] (if (nil? v) m (assoc m k v))) {} m)` | PASS |
| merge+prune | `(merge m1 (into {} (remove (comp nil? val)) m2))` | PASS |
| cond-> | conditional assoc build-up | PASS |
| some-> | nil short-circuit nested access | PASS |
| add-watch | watch fires with new state → Observer replacement | PASS |
| memoize | Flyweight: one call for repeated args | PASS |
| multimethod 2-axis | `(defmulti export (fn [item fmt] [(:kind item) fmt]))` + `:default` | PASS |
| mapv eager | `(mapv #(* % %) [1 2 3])` | PASS |
| thread-last reduce | `(->> items (map #(* (:price %) (:qty %))) (reduce +))` | PASS |
| update-vals | core, 1.11+ | PASS |
| remove-vals | **NOT core** — it's `clojure.set/remove-vals` | NOTED |
| flattened doseq | `(doseq [x xs y ys] ...)` | PASS |
| keyword default | `(:missing m :default)` | PASS |
| reduce-kv map | `(reduce-kv (fn [m k v] (assoc m k (inc v))) {} m)` | PASS |
| ffirst etc. | `(ffirst [[1 2] [3 4]])` → 1 | PASS |
| merge-with nil-pref | `(merge-with (fn [d v] (if (nil? v) d v)) defaults m)` — SKILL.md house rule | PASS |
| builder :pre | `{:pre [(pos? amount)]}` throws AssertionError | PASS |
| Command partial | `(partial f args)` + `(apply f args)` | PASS |
| tree-seq | `(= (tree-seq vector? identity [1 [2 [3]]]) '([1 [2 [3]]] 1 [2 [3]] 2 [3] 3))` | PASS |
| Chain of Resp | `(some #(% msg) handlers)` first non-nil handler | PASS |
| some-> arithmetic | `(some-> {:a {:b 2}} :a :b (* 10))` → 20 | PASS |
| merge/into/concat flips | `(->> base (merge o))` → base wins; `(->> [1] (into [2]))` → `[2 1]`; `(->> [1] (concat [2]))` → `(2 1)` | PASS |
| as-> interleave/take | `(as-> [1 2] v (interleave v [9 9]) (take 1 v))` → `[1]` | PASS |
| some-> parse chain | `(some-> {:p "41"} :p Long/parseLong inc)` → 42 / nil | PASS |
| some->> seq tail | `(some->> {:items [1 2 3]} :items (filter even?) first)` → 2 / nil | PASS |
| doto | `(str (doto (StringBuilder.) (.append "a") (.append "b")))` → "ab" | PASS |
| bare steps | `(-> {:hair-color :gray} :hair-color name)` → "gray" | PASS |
| cond-> no short-circuit | both tests' side effects run: log → `[:t1 :t2]` | PASS |
| cond-> test can't see temp | `(let [n 1] (cond-> n true (inc) (= 2 n) (str :saw)))` → 2, no :saw | PASS |
| nil tolerance | `(count nil)`→0, `(rest nil)`→(), `(str/join (rest nil))`→"" | PASS |
| nil NPEs | `(pos? nil)`/`(zero? nil)`/`(inc nil)`/`(subs nil 1)` → NPE | PASS |
| subs on empty | `(subs "" 1)` → StringIndexOutOfBoundsException; `not-empty` tail → nil | PASS |
| some-> bare interop | `(some-> 1 ((constantly "abc")) (.length))`→3; nil case → nil (method not called) | PASS |
| trim/not-empty chain | `(some-> m :note str/trim not-empty (subs 1))` nil/whitespace→nil, `"  x41  "`→"41" | PASS |
| reduce identities | `(reduce + [])`→0, `(reduce * [])`→1 — explicit init redundant | PASS |
| pair generalization | `(reduce op init (map (partial * 2) xs))` ≡ both originals over `[1 2 3] [] [7] [-1 0 2]`; `partial` gives named API back | PASS |
| every-pred composition | `(every-pred :active? #(<= 3 (:members %)) (comp #{:rock :metal} :genre))` ≡ inline `and` | PASS |
| literal vs fn | big `#(...)` ≡ destructured `fn` tagline builder | PASS |
| keyword stringify | `(str/join " · " [:raw :polish])` → `":raw · :polish"`; `name` strips colon | PASS |
| `{:keys [name]}` shadow | binding `name` then `(map name tags)` → ClassCastException (String as IFn) | PASS |
| = vs == categories | `(= 1 1.0)` → false; `(== 1 1.0)` → true | PASS |
| NaN remove no-op | `(some NaN? (remove #(= % Double/NaN) [9 3 ##NaN 4]))` → true (survives) | PASS |
| NaN set/disj | `(#{1.0 2.0 ##NaN} ##NaN)` → nil; `(count (disj #{2.0 1.0 ##NaN} ##NaN))` → 3 | PASS |
| NaN in sort | our repro returned silently UNSORTED (guide says may throw TimSort IllegalArgumentException — data-dependent) | PASS |
| subtraction cmp floats | `(sort #(- %1 %2) [1.0 0.9 0.8 0.7])` → `(1.0 0.9 0.8 0.7)` — descending! | PASS |
| subtraction cmp longs | `(sort #(- %1 %2) [2147483650 … 4 2 3 1])` → garbage order | PASS |
| `<=` boolean cmp | `sorted-set-by` with `<=`: 3 entries kept but ALL lookups → nil | PASS |
| partial-key sorted-set | `by-2nd` comparator: 3 same-2nd vectors → count 1 | PASS |
| java-list hash key | `(= ArrayList [1 2 3])` true but `(get (hash-map jl 5) [1 2 3])` nil; `(conj #{} jl [1 2 3])` → 2 entries | PASS |
| vec key fix | `(get (hash-map (vec jl) 5) [1 2 3])` → 5 | PASS |
| full-scale bug | `(= (/ 3.0 3.0) 1)` false; `(== ...)` true | PASS |
| vector multi-field cmp | `[(:salary y) (:name x) (:company x)]` ≡ nested-let comparator | PASS |
| tiebreak sort-by | `(sort-by (fn [[id s]] [(- s) id]) …)` deterministic | PASS |
| float/double hash | `(hash (float 1.0e9))` ≠ `(hash (double 1.0e9))` | PASS |
| regex/−0 | `(= #"abc" #"abc")` false; `(= 0.0 -0.0)` true | PASS |
| array-map shield | `(= (.hashCode [1 2]) (.hashCode (ArrayList. [1 2])))` true (994) but `(hash [1 2])`≠994; ≤8-entry maps scan by equals → mixed lookup WORKS; >8 → PersistentHashMap → nil | PASS |
| vec fix at scale | `(vec c)` store-side, 13 pairs, vector lookup → hit | PASS |
| NaN? on nil | NPE — original `=`-filters tolerate nil, NaN? filters don't | PASS |
| subtraction overflow | `#(- %1 %2)` with Long/MAX+MIN → ArithmeticException (vs silent truncation in int-range) | PASS |

Harness bugs found during testing (expected-value mistakes, fixed above):
comparing `keys` to a set; `true?` on truthy seq; wrong expected `into` result;
`=` seq-vs-vector; `some` composed wrong (`some #(% m)` not
`(some identity hs)` then call).
