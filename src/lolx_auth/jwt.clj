(ns lolx-auth.jwt
  (:require 
   [clj-jwt.core  :refer :all]
   [clj-jwt.key   :refer [private-key]]
   [clj-time.core :refer [now plus days]]))


(defn build-claim
  [issuer user-id]
  {:iss issuer
   :exp (plus (now) (days 1))
   :iat (now)
   :sub user-id}
)

(def rsa-prv-key (private-key "rsa/private" "password"))

(defn produce
  [issuer user-id]
  (-> (build-claim issuer user-id) jwt (sign :RS256 rsa-prv-key) to-str))
