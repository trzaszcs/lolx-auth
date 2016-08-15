(ns lolx-auth.register
  (:require 
   [compojure.core :refer :all]
   [lolx-auth.validation :as validation]
   [lolx-auth.dao :as dao]
   [ring.util.response :refer :all]
   [digest :as digest]))

(defn register
  [request]
  (let [{first-name :firstName last-name :lastName email :email password :password} (:body request)]
    (if (not (validation/valid? first-name last-name email password))
      {:status 400}
      (do
       (if (dao/add-user first-name last-name email (digest/sha-256 password))
         {:status 200}
         {:status 409}
         )))))
