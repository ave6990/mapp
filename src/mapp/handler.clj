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
  (GET "/" [] (c/get-verifications empty-query))
  (GET "/verifications/:id" req (response [{:id 1 :name "a"} {:id 2 :name "b"}]) #_(c/get-verifications-table-panel req))
  (route/not-found "Not Found"))

(def app
  (->
    app-routes
    (middleware/wrap-json-body)
    (middleware/wrap-json-response)
    (wrap-defaults site-defaults)))
