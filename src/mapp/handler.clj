(ns mapp.handler
  (:require
    [clojure.string :as string]
    [compojure.core :refer :all]
    [compojure.route :as route]
    [ring.util.response :refer [response]]
    [ring.middleware.json :as middleware]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
    [mapp.model.midb :as midb]
    [mapp.views.view :as v]
    [mapp.controller.controller :as c]))

(def empty-query 
  {:params
    {:id "1"
     :q ""
     :limit "20"}})

(defroutes app-routes
  (GET "/" [] (c/get-verifications-page empty-query))
  (GET "/verifications/:id" req (response (c/get-verifications-data req)))
  (GET "/conditions" [] (c/get-conditions-page empty-query))
  (GET "/conditions/:id" req (response (c/get-conditions-data req)))
  #_(GET "/gso" [] (c/get-gso-page empty-query))
  #_(GET "/gso/:id" req (response (c/get-conditions-data req)))
  #_(GET "/references" [] (c/get-references-page empty-query))
  #_(GET "/references/:id" req (response (c/get-references-data req)))
  (route/not-found "Not Found"))

(def app
  (->
    app-routes
    (middleware/wrap-json-body)
    (middleware/wrap-json-response)
    (wrap-defaults site-defaults)))
