(ns lolx-auth.validation-test
  (:use midje.sweet)
  (:require
   [lolx-auth.validation :refer :all]))


(fact "returns true for all not-empty values"
  (registration-valid? "john" "deer" "deer@wp.pl" "pass" "location" ) => true)

(fact "returns false if last-name is empty"
  (registration-valid? "john" "" "deer@wp.pl" "pass" "location") => false)
