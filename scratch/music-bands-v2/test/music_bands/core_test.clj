(ns music-bands.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [music-bands.core :as music-bands]))

(deftest loud?-test
  (testing "metal and punk bands are loud"
    (is (music-bands/loud? (music-bands/make-band "Slayer" :metal)))
    (is (music-bands/loud? (music-bands/make-band "The Offspring" :punk))))
  (testing "other genres are quiet"
    (is (not (music-bands/loud? (music-bands/make-band "Adele" :pop))))))
