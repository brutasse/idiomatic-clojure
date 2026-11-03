;; scratch harness: functional proof that the clojure.test assertion forms
;; used in the skill work as documented. `thrown?` / `thrown-with-msg?` are
;; assert-expr dispatches, usable only as (is (thrown? Type form)).
(ns scratch.test-forms
  (:require [clojure.test :as t]))

(t/deftest demo-test
  (t/testing "arithmetic"
    (t/is (= 1 1))
    (t/are [a b] (= a b) 1 1 2 2)
    (t/is (thrown? ArithmeticException (/ 1 0)))
    (t/is (thrown-with-msg? ArithmeticException #"zero" (/ 1 0)))))
