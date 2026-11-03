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
| dup-set literal | `(read-string "#{1 1}")` / `"{:a 1 :a 2}"` → IllegalArgumentException at the reader | PASS |
| case default | `case` has NO `:else`: `(case :x 1 :one :else :dunno)` throws "No matching clause" (`:else` is a test value); bare final expr is the default: `(case :x 1 :one 2 :two :dunno)` → `:dunno`; no default + no match → IllegalArgumentException | PASS |
| docstring placement | `(defn f [x] "doc" (inc x))` → `(:doc (meta))` nil; string silently becomes first body form; one-form body returns the string instead of computing | PASS |
| io-in-tx | `(dosync (io! ...))` → IllegalStateException; `io!` outside → ok | PASS |
| stm-retry | deterministic retry (T1 pins read → main commits → T1 writes) → atom side effect inside `dosync` runs once per ATTEMPT (`[:attempt :attempt]`) while ref change applies once (r=6). 5/5 runs identical | PASS |
| stm-block | thread parked inside tx holding a write to a ref blocks every other tx touching it — main's conflicting commit deadlocks (the "don't block inside a tx" rule, mechanistically) | PASS |
| ensure-ref | `ensure` takes a **Ref** (touch semantics, returns in-transaction value) — `(dosync (ensure rr) (alter rr + 1))` ok; a boolean CCEs | PASS |
| ref-deref absent | `ref-deref` does NOT exist in 1.12.5 (find-var nil, absent from sources) — in-tx reads are plain `deref`/`@` | NOTED |
| thrown?-dispatch | `clojure.test/thrown?` / `thrown-with-msg?` are NOT public vars — `assert-expr` dispatches; usable only as `(is (thrown? Type form))`; `:refer [thrown?]` / `t/thrown?` fail to compile | NOTED |
| test-forms | `deftest`/`testing`/`are`/`(is (thrown? ...))`/`(is (thrown-with-msg? ...))` functional run: 1 test, 5 assertions, 0 failures | PASS |
| ex-info import | `(catch ExceptionInfo e …)` needs `(import clojure.lang.ExceptionInfo)` in the ns, else compile error | PASS |
| record ctors | `->Pt` / `map->Pt` / `Pt.` all construct; custom ctor `make-customer` with `{:pre [...]}` throws AssertionError | PASS |
| priv meta | `^:private` lands `:private true` in var meta — plus `:line/:column/:file/:name/:ns`, so whole-map equality fails; `defn-` → `:private true`; `@#'ns/var` works | PASS |
| api meta | `:added` / `:deprecated` / `:superseded-by` string values land in var meta (e.g. `{:deprecated "0.2", :superseded-by "loud?"}`) | PASS |
| math-pow | `(clojure.math/pow 2 5)` → 32.0 (with `[clojure.math :as math]`) | PASS |
| parseInt catch | `(catch NumberFormatException)` around `Integer/parseInt` works; `parseInt "99999999999"` throws NFE | PASS |
| prune-flip | `(into {} (remove #(nil? (val %))) {:a 1 :b nil :c 3})` → `{:a 1 :c 3}` (the fn-literal form of the old `comp nil? val` row) | PASS |
| alter-var-root | `(alter-var-root #'thing (fn [_] 2))` rebinds the var | PASS |
| see-also meta | `:see-also` string vector lands in var meta | PASS |
| earmuffs | `^:dynamic` + `binding [*d* …]` works | PASS |
| defrecord docstring | `(defrecord Foo \"doc\" [a b])` → AssertionError \"No fields vector given.\" — `defrecord`'s macro is `[name fields & opts+specs]`, **no docstring slot** (unlike `defn`); the string is bound to `fields`. Stock 1.12.5 behavior (source `core_deftype.clj` + reproduced on nREPL) | NOTED |
| test-no-main | `clojure.test` has NO `main` var in 1.12.5 (`ns-publics` + source) — `:main-opts [\"-m\" \"clojure.test\" …]` NPEs in `clojure.main/main`; run tests via `:exec-fn` or `clj -M -e '(test-ns …)'` | NOTED |
| test-ns rerun | scratch/bands (L rep1 artifact) re-run independently: `{:test 1, :pass 3, :fail 0, :error 0}` | PASS |

Harness bugs found during testing (expected-value mistakes, fixed above):
comparing `keys` to a set; `true?` on truthy seq; wrong expected `into` result;
`=` seq-vs-vector; `some` composed wrong (`some #(% m)` not
`(some identity hs)` then call).
