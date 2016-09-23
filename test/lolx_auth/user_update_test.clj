(ns lolx-auth.user-update-test
  (:use midje.sweet)
  (:require
   [lolx-auth.user :refer :all]
   [lolx-auth.dao :as dao]
   [lolx-auth.jwt :as jwt]
   [digest :as digest]))

(defn request 
  [user-id jwt email first-name last-name location]
  {:params {:user-id user-id}
   :body {:email email :firstName first-name :lastName last-name :location location}
   :headers {"authorization" (str "Bearer " jwt) }})

(fact "should return '401' when bad jwt"
  (let [user-id "234"
        jwt "JWT"
        email "email@com.pl"
        first-name "Julio"
        last-name "Iglesias"
        location "location"]
    (update-account (request user-id jwt email first-name last-name location)) => {:status 401} 
    (provided
     (jwt/ok? jwt user-id) => false)))

(fact "should return '201'"
  (let [user-id "234"
        jwt "JWT"
        email "email@com.pl"
        first-name "Julio"
        last-name "Iglesias"
        location "location"]
    (update-account (request user-id jwt email first-name last-name location)) => {:status 200} 
    (provided
     (dao/update user-id email first-name last-name location) => true
     (jwt/ok? jwt user-id) => true)))
