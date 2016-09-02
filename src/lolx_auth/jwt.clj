(ns lolx-auth.jwt
  (:require 
   [clj-jwt.core  :refer :all]
   [clj-jwt.key   :refer [private-key public-key]]
   [clj-time.core :refer [now plus days]]
   [clojure.java.io :as io]))


(defn build-claim
  [issuer user-id]
  {:iss issuer
   :exp (plus (now) (days 1))
   :iat (now)
   :sub user-id}
)

(def rsa-prv-key (private-key (io/resource "rsa/key") "password"))
(def rsa-pub-key (public-key (io/resource "rsa/key.pub")))

(defn produce
  [issuer user-id]
  (-> (build-claim issuer user-id) jwt (sign :RS256 rsa-prv-key) to-str))

(defn ok?
  [jwt-token subject]
  (let [jwt (str->jwt jwt-token)]
    (and 
     (verify jwt rsa-pub-key)
     (= subject (get-in jwt [:claims :sub]))) 
    ))
