(ns lolx-auth.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :refer [site]]
            [lolx-auth.user :refer [details 
                                    register 
                                    update-account 
                                    change-password 
                                    reset-password 
                                    change-password-after-reset
                                    bulk-details]]
            [lolx-auth.authentication :refer [auth auth-facebook]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [clj-time.format :as format]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (POST "/users" []  register)
  (PUT  "/users/reset-password" [] reset-password)
  (PUT  "/users/reset-password/:reset-ref-id/change-password" [] change-password-after-reset)
  (GET  "/users/bulk" [] bulk-details)
  (GET  "/users/:user-id" [] details)
  (PUT  "/users/:user-id" [] update-account)
  (PUT  "/users/:user-id/change-password" [] change-password)
  (POST "/auth" [] auth)
  (POST "/auth-facebook" [] auth-facebook)
  (route/not-found "Not Found"))

(defonce iso-formatter (format/formatters :date-time))

(defn serialize-date
  [obj]
  (if (sequential? obj)
    (map serialize-date obj)
    (if (map? obj)
      (reduce
       (fn [set entry]
         (assoc set (first entry) (serialize-date (last entry)))
         )
       {}
       obj)
      (if (instance? org.joda.time.DateTime obj)
        (format/unparse iso-formatter obj)
        obj
        )
      )
    ))

(defn date-serializer
  [handler]
  (fn [request]
    (let [response (handler request)
          body (:body response)]
      (if (and body (coll? body))
        (assoc response :body (serialize-date body))
        response
        )
      )))

(def app
  (-> app-routes
      (wrap-json-body {:keywords? true :bigdecimals? true})
      (date-serializer)
      (wrap-json-response)
      (wrap-defaults (assoc-in site-defaults [:security] {:anti-forgery false}))))


(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (site #'app) {:port port :join? false})))
