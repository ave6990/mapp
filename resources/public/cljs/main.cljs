(ns mapp.core
  (:require
    [clojure.string :as string]
    [reagent.core :as r]
    [reagent.dom :as dom]
    [ajax.core :refer [GET POST]]))

(def records-limit
  (atom 20))

(def current-page
  (atom 1))

(def site "http://localhost:3000/")

(defn make-url
  [section page query limit]
  (str site 
       section "/"
       page "?q="
       query "&limit="
       limit))

(defn get-by-id
  [id]
  (.getElementById js/document id))

(defn get-value
  [id]
  (-> (get-by-id id)
      .-value))

(defn add-event-listener
  [id event fn]
  (.addEventListener (get-by-id id) event fn))

(defn page-number-changed
  [event]
  (let [p-num (-> event .-target .-value)]
    (reset! current-page p-num)
    (->
      (js/fetch (make-url
                "verifications"
                p-num
                (get-value "query")
                @records-limit))
      (.then #(.json %))
      (.then #(.log js/console %)))))

(defn add-behavior
  []
  (add-event-listener "page-number" "click" page-number-changed))

(add-behavior)

(comment

(defn page-number-changed
  [event]
  (js/console.log
    (->
      event 
      .-target
      .-value)))

(make-url "gso" 1 "" 20)

(.open js/window (make-url
                   "verifications"
                   p-num
                   (get-value "query")
                   @records-limit))
)
