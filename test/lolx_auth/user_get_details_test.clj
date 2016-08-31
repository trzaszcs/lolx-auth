(ns lolx-auth.user-get-details-test
  (:use midje.sweet)
  (:require
   [lolx-auth.user :refer :all]
   [lolx-auth.dao :as dao]
   [lolx-auth.jwt :as jwt]
   [digest :as digest]))


(fact "should return short details if no jwt token"
  (let [user-id "234"
        first-name "Julio"]
    (get {:params {:user-id user-id}}) => #(get-in % [:body :first-name]) 
    (provided
     (dao/find-by-id user-id) => {:first-name first-name :email "secret-email"})))

(fact "should return full details for jwt token"
  (let [user-id "234"
        first-name "Julio"
        email "secret-email"
        jwt "SOME-JWT"]
    (get {:params {:user-id user-id} :headers {"authorization" (str "Bearer " jwt) }}) 
      => 
    #(get-in % [:body :email]) 
    (provided
     (jwt/ok? jwt) => true
     (dao/find-by-id user-id) => {:first-name first-name :email email})))