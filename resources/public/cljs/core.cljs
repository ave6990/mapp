(ns cljs.core
  (:require
    [clojure.string :as string]
    [reagent.core :as r]
    [reagent.dom :as dom]
    [ajax.core :refer [GET POST PUT]]
    [cljs.dom-functions :refer :all]
    [cljs.table :as table]
    [cljs.table-handlers :as table-handlers]
    [cljs.query-panel-handlers :as query-handlers]
    [cljs.context-menu :as ctx-menu]))

(defn stringify
  [data]
  (.stringify js/JSON
              (clj->js data)))

(defn read-selected-rows
  []
  (let [rows (get-by-class "selected")]
    (vec
      (for [row rows]
           (table/read-row row)))))

;;## context-menu event listener functions
(defn ctx-action-unselect
  [event]
  (table-handlers/unselect-rows))

(defn ctx-action-write
  [event]
  (let [sdata (stringify (read-selected-rows))]
    (table-handlers/unselect-rows)
    (js/fetch "/verifications/save"
      (clj->js {:method "POST"
                :headers {"Content-type" "application/json;charset=utf-8"}
                :body sdata}))))

(defn ctx-action-copy
  [event]
  (let [row (first (get-by-class "selected"))]
    (println "Copy")
    (println (table/read-row row))))

(defn ctx-action-delete
  [event]
  (println "Delete"))

(def menu-actions
  {"ctx-menu-action-write" ctx-action-write
   "ctx-menu-action-copy" ctx-action-copy
   "ctx-menu-action-delete" ctx-action-delete
   "ctx-menu-action-unselect" ctx-action-unselect})

(defn add-context-menu-event-listener
  []
  (doall
    (for [el (get-by-class "context-menu-item")]
         (.addEventListener el "click" (ctx-menu/item-click menu-actions)))))

(defn text-snippets-dragstart
  [event]
  (-> event
      .-dataTransfer
      (.setData "text/plain" (-> event .-target (.getAttribute "name")))))

(defn reload-pressed
  [event]
  (when (= "F5" (-> event .-key))
        (-> event (.preventDefault))))

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
  (add-context-menu-event-listener))

(add-behavior)

(comment

(.open js/window (make-url
                   "verifications"
                   p-num
                   (get-value "query")
                   @records-limit))

)
