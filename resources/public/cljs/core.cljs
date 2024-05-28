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

(defn ctx-action-save
  [event]
  (let [sdata (stringify (read-selected-rows))]
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
                 (read-selected-rows))]
    (println "Delete")
    (set-html "delete-record-numbers" (string/join ", " ids))
    (add-class (get-by-id "delete-popup") "show-popup")))

(defn popup-no-click
  [event]
  (dorun
    (for [popup (get-by-class "popup")]
         (remove-class popup "show-popup"))))

(defn save-popup-yes-click
  [event]
  (let [records (read-selected-rows)]
    (table-handlers/unselect-rows)
    (remove-class (get-by-id "save-popup") "show-popup")
    (js/fetch "/verifications/save"
      (clj->js {:method "POST"
                :headers {"Content-type" "application/json;charset=utf-8"}
                :body (stringify records)}))))

(defn copy-popup-yes-click
  [event]
  (let [rec-num (read-string (get-html "copy-record-number"))
        cnt (read-string (get-value "copy-count"))]
    (.log js/console "Copy record " rec-num cnt " times")
    (table-handlers/unselect-rows)
    (remove-class (get-by-id "copy-popup") "show-popup")
    (js/fetch "/verifications/copy"
      (clj->js {:method "POST"
                :headers {"Content-type" "application/json;charset=utf-8"}
                :body (stringify {:id rec-num
                                  :cnt cnt})}))))

(defn delete-popup-yes-click
  [event]
  (let [data (read-selected-rows)
        ids (map :id
                 data)
        rec-nums (map read-string
                      ids)]
    (table-handlers/unselect-rows)
    (remove-class (get-by-id "delete-popup") "show-popup")
    (println ids)
    (js/fetch "/verifications/delete"
      (clj->js {:method "DELETE"
                :headers {"Content-type" "application/json;charset=utf-8"}
                :body (stringify {:ids rec-nums})}))))

(defn add-popup-event-listeners
  []
  (doall
    (for [but (get-by-class "popup-no")]
         (.addEventListener but "click" popup-no-click)))
  (.addEventListener (get-by-id "copy-popup-yes") "click" copy-popup-yes-click)
  (.addEventListener (get-by-id "delete-popup-yes") "click" delete-popup-yes-click)
  (.addEventListener (get-by-id "save-popup-yes") "click" save-popup-yes-click))

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
  (add-context-menu-event-listener)
  (add-popup-event-listeners))

(add-behavior)

(comment

(.open js/window (make-url
                   "verifications"
                   p-num
                   (get-value "query")
                   @records-limit))

)
