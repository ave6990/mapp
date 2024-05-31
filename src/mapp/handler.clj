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

(def query-save
  (POST "/save" req (response (c/save-records (:body req)))))

(def query-copy
  (POST "/copy" req (response (c/copy-record (:body req)))))

(def query-delete
  (DELETE "/delete" req (response (c/delete-records (:body req)))))

(defroutes app-routes
  (GET "/" req (c/get-verifications-page (:params req)))
  (context "/verifications" []
    (GET "/get" req (response (c/get-verifications-data (:params req))))
    (POST "/save" req (response (c/write-verifications (:body req))))
    (POST "/copy" req (response (c/copy-verifications (:body req))))
    (POST "/gen-value" req (response (c/gen-value-verifications (:body req))))
    (DELETE "/verifications/delete" req (response (c/delete-verifications (:body req)))))
  (context "/conditions" []
    (GET "/" req (c/get-conditions-page (:params req)))
    (GET "/get" req (response (c/get-conditions-data (:params req))))
    query-save
    query-copy
    query-delete)
  (context "/verification-operations" []
    (GET "/" req (c/get-verification-operations-page (:params req)))
    (GET "/get" req (response (c/get-verification-operations-data (:params req))))
    query-save
    query-copy
    query-delete)
  (context "/gso" []
    (GET "/" req (c/get-gso-page (:params req)))
    (GET "/get" req (response (c/get-gso-data (:params req))))
    query-save
    query-copy
    query-delete)
  (context "/references" []
    (GET "/" req (c/get-references-page (:params req)))
    (GET "/get" req (response (c/get-references-data (:params req))))
    query-save
    query-copy
    query-delete)
  (context "/counteragents" []
    (GET "/" req (c/get-counteragents-page (:params req)))
    (GET "/get" req (response (c/get-counteragents-data (:params req))))
    query-save
    query-copy
    query-delete)
  (context "/methodology" []
    (GET "/" req (c/get-methodology-page (:params req)))
    (GET "/get" req (response (c/get-methodology-data (:params req))))
    query-save
    query-copy
    query-delete)
  (context "/v-operations" []
    (GET "/" req (c/get-v-operations-page (:params req)))
    (GET "/get" req (response (c/get-operations-data (:params req))))
    query-save
    query-copy
    query-delete)
  (context "/refs-set" []
    (GET "/" req (c/get-refs-set-page (:params req)))
    (GET "/get" req (response (c/get-refs-set-data (:params req))))
    (POST "/save" req (response (c/write-refs-set (:body req))))
    (POST "/copy" req (response (c/copy-refs-set (:body req))))
    (DELETE "/delete" req (response (c/delete-refs-set (:body req)))))
  (context "/measurements" []
    (GET "/" req (c/get-measurements-page (:params req)))
    (GET "/get" req (response (c/get-measurements-data (:params req))))
    query-save
    query-copy
    query-delete)
  (context "/channels" []
    (GET "/" req (c/get-channels-page (:params req)))
    (GET "/get" req (response (c/get-channels-data (:params req))))
    query-save
    query-copy
    query-delete)
  (context "/metrology" []
    (GET "/" req (c/get-metrology-page (:params req)))
    (GET "/get" req (response (c/get-metrology-data (:params req))))
    query-save
    query-copy
    query-delete)
  (GET "/protocols" req (c/get-protocols (:params req)))
  (route/not-found "Not Found"))

(def app
  (->
    app-routes
    (middleware/wrap-json-body {:keywords true})
    (middleware/wrap-json-response)
    (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))))  ;;  TOFIX anti-forgery?
