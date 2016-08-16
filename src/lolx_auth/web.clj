(ns lolx-auth.web
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [lolx-auth.handler :as handler]
            [ring.adapter.jetty :as jetty]))


(defn -main [& [port]]
  (let [port (Integer. (or port 5000))]
    (jetty/run-jetty (site #'handler/app) {:port port :join? false})))
