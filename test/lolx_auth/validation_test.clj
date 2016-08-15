(ns lolx-auth.validation-test
  (:use midje.sweet)
  (:require
   [lolx-auth.validation :refer :all]))


(fact "returns true for all not-empty values"
  (valid? "john" "deer" "deer@wp.pl" "pass" ) => true)

(fact "returns false if last-name is empty"
  (valid? "john" "" "deer@wp.pl" "pass" ) => false)
