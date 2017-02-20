(ns lolx-auth.jwt
  (:require 
   [clj-jwt.core  :refer :all]
   [clj-jwt.key   :refer [private-key public-key]]
   [clj-time.core :refer [now plus days seconds]]
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

(defn get-rsa-pub-key!
  [issuer]
  (public-key (io/resource (str "rsa/" issuer ".pub"))))

(defn produce
  [issuer user-id]
  (-> (build-claim issuer user-id) jwt (sign :RS256 rsa-prv-key) to-str))

(defn ok?
  [jwt-token subject]
  (let [jwt (str->jwt jwt-token)
        claims (get jwt :claims)
        issuer (:iss claims)]
    (and
     (verify jwt (get-rsa-pub-key! issuer))
     (= subject (get-in jwt [:claims :sub])))
    ))

(defn extract-token
  [headers]
  (let [authorization-header (get headers "authorization")]
    (when (not (nil? authorization-header))
      (clojure.string/replace-first authorization-header #"Bearer " ""))))

(defn build-header
  [issuer to]
  {"Authorization" (str "Bearer " (produce issuer to))})
