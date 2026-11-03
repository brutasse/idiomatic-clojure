(ns bands.core-test
  (:require [bands.core :as bands]
            [clojure.test :refer :all]))

(deftest loud?-test
  (testing "metal and punk bands are loud"
    (are [name genre] (bands/loud? (bands/make-band name genre))
      "Metallica" :metal
      "Black Flag" :punk))
  (testing "other genres are not loud"
    (is (not (bands/loud? (bands/make-band "ABBA" :pop)))
        "pop band is not loud")))
