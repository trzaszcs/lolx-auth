(ns lolx-auth.authentication
  (:require 
   [compojure.core :refer :all]
   [lolx-auth.dao :as dao]
   [lolx-auth.jwt :as jwt]
   [ring.util.response :refer :all]
   [digest :as digest]))

(defn auth
  [request]
  (let [{email :email password :password} (:body request)
        user (dao/find email (digest/sha-256 password))
        user-id (:id user)]
    (if (nil? user)
      {:status 401}
      {:body {:jwt (jwt/produce "frontend" user-id) :userId user-id }}
      )))
