(ns lolx-auth.fb-authentication-test
  (:use midje.sweet)
  (:require
   [lolx-auth.authentication :refer :all]
   [lolx-auth.dao :as dao]
   [lolx-auth.jwt :as jwt]
   [lolx-auth.facebook :as fb]
   [digest :as digest]))

(defn request
  [code]
  {:body {:code code}})

(fact "should return JWT if fb-user-id exists"
  (let [code "CODE"
        fb-id "FB-ID"
        access-token "AT"
        jwt "SomeJWT"
        user-id "23434"]
    (auth-facebook (request code)) => {:body {:jwt jwt :userId user-id}}
    (provided
     (fb/access-token! code) => access-token
     (fb/user-details! access-token) => {:id fb-id}
     (dao/find-by-fb-id fb-id) => {:id user-id}
     (jwt/produce "frontend" user-id) => jwt)))

(fact "should return JWT and match fb-user-id for new fblogin"
  (let [code "CODE"
        fb-id "FB-ID"
        email "some@emailcom"
        access-token "AT"
        jwt "SomeJWT"
        user-id "23434"]
    (auth-facebook (request code)) => {:body {:jwt jwt :userId user-id}}
    (provided
     (fb/access-token! code) => access-token
     (fb/user-details! access-token) => {:id fb-id :email email}
     (dao/find-by-fb-id fb-id) => nil
     (dao/find-by-email email) => {:id user-id}
     (dao/link-fb-account  user-id fb-id) => true
     (jwt/produce "frontend" user-id) => jwt)))
