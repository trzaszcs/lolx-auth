(ns lolx-auth.user
  (:require 
   [compojure.core :refer :all]
   [lolx-auth.validation :as validation]
   [lolx-auth.dao :as dao]
   [lolx-auth.jwt :as jwt]
   [ring.util.response :refer :all]))

(defn- extract-jwt
  [headers]
  (let [authorization-header (get headers "authorization")]
    (when (not (nil? authorization-header))
      (clojure.string/replace-first authorization-header #"Bearer " ""))))

(defn- get-user
  [user-id jwt]
  (let [details (dao/find-by-id user-id)]
    (if (and (not (nil? jwt)) (jwt/ok? jwt))
      (dissoc details :password)
      (dissoc details :password :email)
      )
))


(defn details
  [request]
  (let [user-id (:user-id (:params request))
        jwt (extract-jwt (:headers request))]
    (if (nil? user-id)
      {:status 400}
      {:body (get-user user-id jwt)})))
