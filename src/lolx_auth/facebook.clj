(ns lolx-auth.facebook
  (:require [clj-http.client :as http]
            [clojure.data.json :as json]))

(def client-id "10698494897173171")
(def redirect-uri "http://lolx-front.herokuapp.com/login")
(def secret "95cacd503b559bd532b7764e7d508add1")


(defn- exchange-token!
  [code]
  (http/get 
   "https://graph.facebook.com/v2.3/oauth/access_token"
   {:query-params {
             :client_id client-id
             :redirect_uri redirect-uri
             :client_secret secret
             :code code}}))

(defn access-token!
  "exchanges token to access token"
  [code]
  (:access_token (json/read-str (exchangeToken code) :key-fn keyword)))

