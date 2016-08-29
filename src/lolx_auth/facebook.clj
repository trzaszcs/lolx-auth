(ns lolx-auth.facebook
  (:require [clj-http.client :as http]
            [clojure.data.json :as json]))

(def client-id "1069849489717317")
(def redirect-uri "http://lolx-front.herokuapp.com/#!/fb")
(def secret "95cacd503b559bd532b7764e7d508add")

(defn- encode
  [str]
  (java.net.URLEncoder/encode str "UTF-8"))

(defn- json
  [str]
  (json/read-str json :key-fn keyword))

(defn- http-exchange-token!
  [code]
  (http/get 
   "https://graph.facebook.com/v2.7/oauth/access_token"
   {:query-params {
             :client_id client-id
             :redirect_uri (encode redirect-uri)
             :client_secret secret
             :code code}}))

(defn- http-user-details!
  [access-token]
  (http/get 
   "https://graph.facebook.com/v2.7/me?"
   {:query-params {:access_token access-token
                   :fields ["id" "first_name" "last_name" "email" "location" "hometown"]}}))

(defn access-token!
  "exchanges token to access token"
  [code]
  (:access_token (json ((http-exchange-token! code) :body))))


(defn user-details!
  [access-token]
  (let [response (http-user-details! access-token)
        jsn (json (response :body))]
    (if (= 200 (response :status))
      (assoc {} :first-name (jsn :first_name) :last-name (jsn :last_name) :email (jsn :email) :location (or (json :location) (json :hometown) :id (jsn :id)))
      nil
      )
    ))
