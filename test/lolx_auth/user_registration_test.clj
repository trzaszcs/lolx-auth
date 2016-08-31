(ns lolx-auth.user-registration-test
  (:use midje.sweet)
  (:require
   [lolx-auth.user :refer :all]
   [lolx-auth.dao :as dao]
   [digest :as digest]))

(defn request
  [first-name last-name email password state city]
  {:body {:firstName first-name :lastName last-name :email email :password password :state state :city city}}
)

(fact "if validation failed it should return '400'"
  (register (request "john" "deer" "deer@wp.pl" "" "state" "city")) => {:status 400})

(fact "should return '409' if add-user returns false"
  (let [first-name "john"
        last-name "deer"
        email "deer@wp.pl"
        password "pass"
        city "city"
        state "state"]
    (register (request first-name last-name email password state city)) => {:status 409}
    (provided
     (dao/add-user 
      anything
      first-name 
      last-name 
      email
      state
      city
      (digest/sha-256 password)) => false)))

(fact "should return '200' if add-user returns true"
  (let [first-name "john"
        last-name "deer"
        email "deer@wp.pl"
        password "pass"
        city "city"
        state "state"]
    (register (request first-name last-name email password state city)) => {:status 200}
    (provided
     (dao/add-user 
      anything
      first-name 
      last-name 
      email 
      state
      city
      (digest/sha-256 password)) => true)
    ))
