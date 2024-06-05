(ns cljs.core
  (:require
    [clojure.string :as string]
    [reagent.core :as r]
    [reagent.dom :as dom]
    [cljs.dom-functions :refer :all]
    [cljs.table :as table]
    [cljs.table-handlers :as table-handlers]
    [cljs.query-panel-handlers :as query-handlers]
    [cljs.popups :as popups]
    [cljs.context-menu :as ctx-menu]
    [cljs.context-menu-handlers :as ctx-menu-handlers]))

(defn text-snippets-dragstart
  [event]
  (-> event
      .-dataTransfer
      (.setData "text/plain" (-> event .-target (.getAttribute "name")))))

(defn reload-pressed
  [event]
  (when (= "F5" (-> event .-key))
        (-> event (.preventDefault))
        (query-handlers/execute-query)
        (table-handlers/unselect-rows)
        #_(status! "Данные обновлены!")))

(defn add-reload-event-listener
  []
  (-> js/document
      .-body
      (.addEventListener "keydown" reload-pressed)))

(defn add-behavior
  []
  (query-handlers/add-event-listeners)
  (doall
    (for [el (get-by-class "text-snippets")]
         (do
           (.addEventListener el "dragstart" text-snippets-dragstart))))
  (add-reload-event-listener)
  (table-handlers/add-event-listeners)
  (ctx-menu-handlers/add-event-listeners)
  (popups/add-popup-event-listeners))

(add-behavior)
