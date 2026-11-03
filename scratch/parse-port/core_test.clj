(ns parse-port.core-test
  (:require [clojure.test :refer [deftest is run-tests testing]]
            [parse-port.core :as core]))

(def ^:private port-file "/home/brute/code/brutasse/idiomatic-clojure/scratch/parse-port/port.txt")

(defn- with-port-file [content f]
  (spit port-file content)
  (f port-file))

(deftest parse-port-test
  (is (string? (:doc (meta #'core/parse-port))))
  (testing "returns the port as an integer"
    (is (= 8080 (with-port-file "8080\n" core/parse-port)))
    (is (= 1 (with-port-file "1" core/parse-port)))
    (is (= 65535 (with-port-file "65535\n" core/parse-port))))
  (testing "ignores surrounding whitespace"
    (is (= 443 (with-port-file "  443  \n" core/parse-port))))
  (testing "throws IllegalArgumentException including the offending content"
    (doseq [bad ["0" "65536" "-80" "abc" "" "80\n90" "99999999999999999999"]]
      (is (thrown-with-msg? java.lang.IllegalArgumentException
                            (re-pattern bad)
                            (with-port-file bad core/parse-port))))))

(deftest slugify-test
  (is (string? (:doc (meta #'core/slugify))))
  (is (= "hello-world" (core/slugify "Hello World")))
  (is (= "a-b-c" (core/slugify "A  B\nC"))))

(deftest truncate-test
  (is (string? (:doc (meta #'core/truncate))))
  (is (= "abc" (core/truncate "abcdef" 3)))
  (is (= "abcdef" (core/truncate "abcdef" 6))))
