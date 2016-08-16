(ns lolx-auth.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [lolx-auth.register :refer [register]]
            [lolx-auth.authentication :refer [auth]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (POST "/register" []  register)
  (POST "/auth" [] auth)
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-json-response)
      (wrap-json-body {:keywords? true :bigdecimals? true})
      (wrap-defaults (assoc-in site-defaults [:security] {:anti-forgery false}))))
