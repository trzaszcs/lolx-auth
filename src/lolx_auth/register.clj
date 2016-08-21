(ns lolx-auth.register
  (:require 
   [compojure.core :refer :all]
   [lolx-auth.validation :as validation]
   [lolx-auth.dao :as dao]
   [ring.util.response :refer :all]
   [digest :as digest]))


(defn gen-id!
  []
  (str (java.util.UUID/randomUUID)))


(defn register
  [request]
  (let [{first-name :firstName 
         last-name :lastName 
         email :email 
         password :password 
         city :city 
         state :state} (:body request)]
    (if (not (validation/registration-valid? first-name last-name email password state city))
      {:status 400}
      (do
       (if (dao/add-user (gen-id!) first-name last-name email state city (digest/sha-256 password))
         {:status 200}
         {:status 409}
         )))))
