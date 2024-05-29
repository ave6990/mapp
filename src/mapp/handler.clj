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
  (GET "/verifications/get" req (response (c/get-verifications-data (:params req))))
  (POST "/verifications/save" req (response (c/write-verifications (:body req))))
  (POST "/verifications/copy" req (response (c/copy-verifications (:body req))))
  (DELETE "/verifications/delete" req (response (c/delete-verifications (:body req))))
  (GET "/conditions" req (c/get-conditions-page (:params req)))
  (GET "/conditions/get" req (response (c/get-conditions-data (:params req))))
  (POST "/conditions/save" req (response (c/save-records (:body req))))
  (POST "/conditions/copy" req (response (c/copy-record (:body req))))
  (DELETE "/conditions/delete" req (response (c/delete-records (:body req))))
  (GET "/gso" req (c/get-gso-page (:params req)))
  (GET "/gso/get" req (response (c/get-gso-data (:params req))))
  (GET "/references" req (c/get-references-page (:params req)))
  (GET "/references/get" req (response (c/get-references-data (:params req))))
  (GET "/counteragents" req (c/get-counteragents-page (:params req)))
  (GET "/counteragents/get" req (response (c/get-counteragents-data req)))
  (GET "/methodology" req (c/get-methodology-page (:params req)))
  (GET "/methodology/get" req (response (c/get-methodology-data (:params req))))
  (GET "/operations" req (c/get-operations-page (:params req)))
  (GET "/operations/get" req (response (c/get-operations-data (:params req))))
  (POST "/operations/save" req (response (c/save-records (:body req))))
  (POST "/operations/copy" req (response (c/copy-record (:body req))))
  (DELETE "/operations/delete" req (response (c/delete-records (:body req))))
  (GET "/refs-set" req (c/get-refs-set-page (:params req)))
  (GET "/refs-set/get" req (response (c/get-refs-set-data (:params req))))
  (GET "/measurements" req (c/get-measurements-page (:params req)))
  (GET "/measurements/get" req (response (c/get-measurements-data (:params req))))
  (POST "/measurements/save" req (response (c/save-records (:body req))))
  (POST "/measurements/copy" req (response (c/copy-record (:body req))))
  (DELETE "/measurements/delete" req (response (c/delete-records (:body req))))
  (route/not-found "Not Found"))

(def app
  (->
    app-routes
    (middleware/wrap-json-body {:keywords true})
    (middleware/wrap-json-response)
    (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))))  ;;  TOFIX anti-forgery?
