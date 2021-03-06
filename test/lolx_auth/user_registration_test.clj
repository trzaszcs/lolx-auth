(ns lolx-auth.user-registration-test
  (:use midje.sweet)
  (:require
   [lolx-auth.user :refer :all]
   [lolx-auth.dao :as dao]
   [digest :as digest]))

(defn request
  [first-name last-name nick email phone password location]
  {:body {:firstName first-name :lastName last-name :nick nick :email email :phone phone :password password :location location}}
)

(fact "if validation failed it should return '400'"
  (register (request "john" "deer" "nick" "deer@wp.pl" "222 222 22" "" "location")) => {:status 400})

(fact "should return '409' if add-user returns false"
  (let [first-name "john"
        last-name "deer"
        email "deer@wp.pl"
        nick "supernick"
        phone "234 3 43 4"
        password "pass"
        location "location"]
    (register (request first-name last-name nick email phone password location)) => {:status 409}
    (provided
     (dao/nick-unique? nick) => true
     (dao/email-unique? email) => true
     (dao/add-user
      anything
      first-name
      last-name
      nick
      email
      phone
      location
      (digest/sha-256 password)) => false)))

(fact "should return '200' if add-user returns true"
  (let [first-name "john"
        last-name "deer"
        nick "supernick"
        email "deer@wp.pl"
        phone "2343 3 43 43 4"
        password "pass"
        location "location"]
    (register (request first-name last-name nick email phone password location)) => {:status 200}
    (provided
     (dao/nick-unique? nick) => true
     (dao/email-unique? email) => true
     (dao/add-user
      anything
      first-name
      last-name
      nick
      email
      phone
      location
      (digest/sha-256 password)) => true)
    ))
