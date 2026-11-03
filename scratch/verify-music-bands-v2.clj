;; Throwaway verification for scratch/music-bands-v2 — not part of the library.
(require '[clojure.test :as t]
         '[music-bands.core :as mb])

(load-file "/home/brute/code/brutasse/idiomatic-clojure/scratch/music-bands-v2/test/music_bands/core_test.clj")

(let [r (t/run-tests 'music-bands.core-test)]
  (t/is (= 0 (:fail r)))
  (t/is (= 0 (:error r)))
  ;; validation: empty, nil, and non-string names all throw
  (t/is (thrown? IllegalArgumentException (mb/make-band "" :rock)))
  (t/is (thrown? IllegalArgumentException (mb/make-band nil :rock)))
  (t/is (thrown? IllegalArgumentException (mb/make-band 42 :rock)))
  ;; make-band returns the record, equal to the ->Band construction
  (t/is (record? (mb/make-band "A" :rock)))
  (t/is (= (mb/->Band "A" :rock) (mb/make-band "A" :rock)))
  ;; shout! still works
  (t/is (= "SLAYER" (mb/shout! (mb/make-band "Slayer" :metal))))
  ;; deprecation metadata on the var
  (t/is (= true (get (meta #'mb/shout!) :deprecated)))
  (t/is (= "loud?" (get (meta #'mb/shout!) :superseded-by)))
  ;; docstrings landed in :doc (placement trap check)
  (t/is (string? (get (meta #'mb/make-band) :doc)))
  (t/is (string? (get (meta #'mb/loud?) :doc)))
  (t/is (string? (get (meta #'mb/shout!) :doc)))
  (t/is (string? (:doc (meta (the-ns 'music-bands.core)))))
  ;; private helper is private, reachable via @#'
  (t/is (true? (get (meta #'mb/loud-genre?) :private)))
  (t/is (= :punk (@#'mb/loud-genre? :punk)))
  (println (str "VERIFY OK: " (:test r) " test(s), "
                (:pass r) " assertion(s), "
                (:fail r) " failures, " (:error r) " errors; self-checks passed")))
