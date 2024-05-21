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
     :limit "100"}})

(defroutes app-routes
  (GET "/" [] (c/get-verifications-page empty-query))
  (GET "/verifications/:id" req (response (c/get-verifications-data req)))
  (GET "/conditions" [] (c/get-conditions-page empty-query))
  (GET "/conditions/:id" req (response (c/get-conditions-data req)))
  (GET "/gso" [] (c/get-gso-page empty-query))
  (GET "/gso/:id" req (response (c/get-gso-data req)))
  (GET "/references" [] (c/get-references-page empty-query))
  (GET "/references/:id" req (response (c/get-references-data req)))
  (GET "/counteragents" [] (c/get-counteragents-page empty-query))
  (GET "/counteragents/:id" req (response (c/get-counteragents-data req)))
  (GET "/methodology" [] (c/get-methodology-page empty-query))
  (GET "/methodology/:id" req (response (c/get-methodology-data req)))
  (route/not-found "Not Found"))

(def app
  (->
    app-routes
    (middleware/wrap-json-body)
    (middleware/wrap-json-response)
    (wrap-defaults site-defaults)))
