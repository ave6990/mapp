(ns mapp.handler
  (:require
    [clojure.string :as string]
    [compojure.core :refer :all]
    [compojure.route :as route]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
    [mapp.model.midb :as midb]
    [mapp.views.verifications :as v]))

(defroutes app-routes
  (GET "/" [] (v/get-verifications ""))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
