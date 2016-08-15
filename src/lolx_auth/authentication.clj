(ns lolx-auth.authentication
  (:require 
   [compojure.core :refer :all]
   [lolx-auth.dao :as dao]
   [ring.util.response :refer :all]
   [digest :as digest]))

(defn auth
  [request]
  (let [{email "email" password "password"} (:body request)
        user (dao/find email(digest/sha-256 password))]
   
    (if (nil? user)
      (response {:status 401})
      (response {:status 200}))))
