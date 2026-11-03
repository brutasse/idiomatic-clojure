(ns music-bands.core-test
  (:require [clojure.test :refer :all]
            [music-bands.core :refer [loud? make-band]]))

(deftest test-loud?
  (is (loud? (make-band "Slayer" :metal)))
  (is (loud? (make-band "Bolt Thrower" :punk)))
  (is (not (loud? (make-band "Adele" :pop)))))
