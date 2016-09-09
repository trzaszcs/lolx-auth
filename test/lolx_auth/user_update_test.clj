(ns lolx-auth.user-update-test
  (:use midje.sweet)
  (:require
   [lolx-auth.user :refer :all]
   [lolx-auth.dao :as dao]
   [lolx-auth.jwt :as jwt]
   [digest :as digest]))

(defn request 
  [user-id jwt email first-name last-name state city]
  {:params {:user-id user-id}
   :body {:email email :firstName first-name :lastName last-name :state state :city city}
   :headers {"authorization" (str "Bearer " jwt) }})

(fact "should return '401' when bad jwt"
  (let [user-id "234"
        jwt "JWT"
        email "email@com.pl"
        first-name "Julio"
        last-name "Iglesias"
        city "city"
        state "state"]
    (update-account (request user-id jwt email first-name last-name state city)) => {:status 401} 
    (provided
     (jwt/ok? jwt user-id) => false)))

(fact "should return '201'"
  (let [user-id "234"
        jwt "JWT"
        email "email@com.pl"
        first-name "Julio"
        last-name "Iglesias"
        city "city"
        state "state"]
    (update-account (request user-id jwt email first-name last-name state city)) => {:status 200} 
    (provided
     (dao/update user-id email first-name last-name state city) => true
     (jwt/ok? jwt user-id) => true)))
