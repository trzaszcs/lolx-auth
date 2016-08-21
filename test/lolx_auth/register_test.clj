(ns lolx-auth.register-test
  (:use midje.sweet)
  (:require
   [lolx-auth.register :refer :all]
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
        state "state"
        fake-id "gen-id"]
    (register (request first-name last-name email password state city)) => {:status 409}
    (provided
     (gen-id!) => fake-id
     (dao/add-user 
      fake-id
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
        state "state"
        fake-id "gen-id"]
    (register (request first-name last-name email password state city)) => {:status 200}
    (provided
     (gen-id!) => fake-id
     (dao/add-user 
      fake-id
      first-name 
      last-name 
      email 
      state
      city
      (digest/sha-256 password)) => true)
    ))
