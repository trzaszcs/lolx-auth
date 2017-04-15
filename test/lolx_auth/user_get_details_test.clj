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
    (details {:params {:user-id user-id}}) => #(get-in % [:body "firstName"]) 
    (provided
     (dao/find-by-id user-id) => {:first-name first-name :email "secret-email"})))

(fact "should return full details for jwt token"
  (let [user-id "234"
        first-name "Julio"
        email "secret-email"
        jwt "SOME-JWT"]
    (details {:params {:user-id user-id} :headers {"authorization" (str "Bearer " jwt) }}) 
      => 
    #(get-in % [:body "email"]) 
    (provided
     (jwt/ok? jwt user-id) => true
     (dao/find-by-id user-id) => {:first-name first-name :email email})))

(fact "should return bulk details"
      (let [user-id-1 "1"
            first-name-1 "Julio"
            user-id-2 "2"
            first-name-2 "Sebastien"]
        (bulk-details {:params {:userId [user-id-1 user-id-2]}}) => {:body {user-id-1 {"firstName" first-name-1} user-id-2 {"firstName" first-name-2}}}
        (provided
         (dao/find-by-id user-id-1) => {:first-name first-name-1 :email "secret-email"}
         (dao/find-by-id user-id-2) => {:first-name first-name-2 :email "secret-email"}
         )))

(fact "should return 404 for not existing user"
      (let [user-id "234"
            first-name "Julio"
            email "secret-email"
            jwt "SOME-JWT"]
        (details {:params {:user-id user-id} :headers {"authorization" (str "Bearer " jwt) }}) 
        => 
        {:status 404} 
        (provided
         (jwt/ok? jwt user-id) => true
         (dao/find-by-id user-id) => nil)))

