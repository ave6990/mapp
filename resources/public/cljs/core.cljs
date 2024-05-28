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
    [cljs.context-menu :as ctx-menu]))

;;## context-menu event listener functions
(defn ctx-action-unselect
  [event]
  (table-handlers/unselect-rows))

(defn ctx-action-save
  [event]
  (let [sdata (stringify (table/read-selected-rows))]
    (println "Save")
    (add-class (get-by-id "save-popup") "show-popup")))

(defn ctx-action-copy
  [event]
  (let [row (first (get-by-class "selected"))
        data (table/read-row row)]
    (println "Copy")
    (set-html "copy-record-number" (:id data))
    (add-class (get-by-id "copy-popup") "show-popup")))

(defn ctx-action-delete
  [event]
  (let [ids (map :id
                 (table/read-selected-rows))]
    (println "Delete")
    (set-html "delete-record-numbers" (string/join ", " ids))
    (add-class (get-by-id "delete-popup") "show-popup")))

(def menu-actions
  {"ctx-menu-action-save" ctx-action-save
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
        (-> event (.preventDefault))
        (query-handlers/execute-query)))

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
  (add-context-menu-event-listener)
  (popups/add-popup-event-listeners))

(add-behavior)

(comment

(.open js/window (make-url
                   "verifications"
                   p-num
                   (get-value "query")
                   @records-limit))

)
