(ns lolx-auth.user
  (:require 
   [compojure.core :refer :all]
   [lolx-auth.validation :as validation]
   [lolx-auth.dao :as dao]
   [lolx-auth.jwt :as jwt]
   [clj-time.format :as format]
   [digest :as digest]
   [ring.util.response :refer :all]))

(defn- extract-jwt
  [headers]
  (let [authorization-header (get headers "authorization")]
    (when (not (nil? authorization-header))
      (clojure.string/replace-first authorization-header #"Bearer " ""))))

(defn- get-user
  [user-id jwt]
  (let [details (dao/find-by-id user-id)]
    (if (and jwt (jwt/ok? jwt))
      (dissoc details :password)
      (dissoc details :password :email :lastName :city :state)
      )
))

(defn serialize
  [user]
  (let [iso-formatter (format/formatters :date-time)]
    (assoc user :created (format/unparse iso-formatter (:created user)))
    ))

(defn gen-id!
  []
  (str (java.util.UUID/randomUUID)))

(defn get
  [request]
  (let [user-id (:user-id (:params request))
        jwt (extract-jwt (:headers request))]
    (if (nil? user-id)
      {:status 400}
      {:body (serialize (get-user user-id jwt))})))


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
