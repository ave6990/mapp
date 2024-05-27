(ns mapp.handler
  (:require
    [clojure.string :as string]
    [clojure.pprint :refer [pprint]]
    [compojure.core :refer :all]
    [compojure.route :as route]
    [ring.util.response :refer [response]]
    [ring.middleware.json :as middleware]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
    [mapp.model.midb :as midb]
    [mapp.views.view :as v]
    [mapp.controller.controller :as c]))

(defroutes app-routes
  (GET "/" req (c/get-verifications-page (:params req)))
  (GET "/verifications/:id" req (response (c/get-verifications-data (:params req))))
  (POST "/add-verifications" req (response (pprint req)) #_(c/write-verifications (:body req)))
  (GET "/conditions" req (c/get-conditions-page (:params req)))
  (GET "/conditions/:id" req (response (c/get-conditions-data (:params req))))
  (GET "/gso" req (c/get-gso-page (:params req)))
  (GET "/gso/:id" req (response (c/get-gso-data (:params req))))
  (GET "/references" req (c/get-references-page (:params req)))
  (GET "/references/:id" req (response (c/get-references-data (:params req))))
  (GET "/counteragents" req (c/get-counteragents-page (:params req)))
  (GET "/counteragents/:id" req (response (c/get-counteragents-data req)))
  (GET "/methodology" req (c/get-methodology-page (:params req)))
  (GET "/methodology/:id" req (response (c/get-methodology-data (:params req))))
  (route/not-found "Not Found"))

(def app
  (->
    app-routes
    (middleware/wrap-json-body {:keywords true})
    (middleware/wrap-json-response)
    (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))))  ;;  TOFIX anti-forgery?
