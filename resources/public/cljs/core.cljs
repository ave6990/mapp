(ns cljs.core
  (:require
    [clojure.string :as string]
    [reagent.core :as r]
    [reagent.dom :as dom]
    [cljs.table :as table]
    [cljs.dom-functions :refer :all]
    [ajax.core :refer [GET POST]]))

(def records-limit
  (r/atom 100))

(def current-page
  (r/atom 1))

(def site "http://localhost:3000/")

(defn make-url
  [section page query limit]
  (str site 
       section "/"
       page "?q="
       query "&limit="
       limit))

(defn get-position
  [event]
    {:x (-> event .-pageX) 
     :y (-> event .-pageY)})

(defn toggle-select-row
  [tr]
  (toggle-class tr "selected"))

(defn unselect-rows
  []
  (doall
    (for [el (get-by-tag "tr")]
         (remove-class el "selected"))))

;;#context#menu
(defn context-menu-on
  []
  (add-class (get-by-id "context-menu")
             "context-menu-active"))

(defn context-menu-off
  []
  (remove-class (get-by-id "context-menu")
                "context-menu-active"))

(defn td-contextmenu
  [event]
  (let [menu (get-by-id "context-menu")
        pos (get-position event)]
    (-> event (.preventDefault))
    (context-menu-on)
    (set! (-> menu .-style .-left) (:x pos))
    (set! (-> menu .-style .-top) (:y pos))))

;;## table event listener
(defn td-clicked
  [event]
  (let [tr (-> event .-target .-parentNode)]
    (toggle-select-row tr)
    (context-menu-off)))

(defn add-table-event-listeners
  []
  (doall
    (for [el (get-by-tag "td")]
         (do
           (.addEventListener el "click" td-clicked)
           (.addEventListener el "contextmenu" td-contextmenu)))))

;;## context-menu event listener
(defn ctx-action-unselect
  [event]
  (unselect-rows))

(defn ctx-action-copy
  [event]
  (println "Copy"))

(defn ctx-action-delete
  [event]
  (println "Delete"))

(defn context-menu-item-click
  [event]
  (let [id (-> event
               .-target
               (.getAttribute "name"))
        menu (get-by-id "context-menu")
        menu-actions {"ctx-menu-action-copy" ctx-action-copy
                      "ctx-menu-action-delete" ctx-action-delete
                      "ctx-menu-action-unselect" ctx-action-unselect}]
    (context-menu-off)
    ((menu-actions id))))

(defn add-context-menu-event-listener
  []
  (doall
    (for [el (get-by-class "context-menu-item")]
         (.addEventListener el "click" context-menu-item-click))))

(defn render-table
  [resp]
  (let [table-panel (get-by-id "table-panel")
        id (-> table-panel .-firstChild .-id)
        {:keys [model data recs-count pages page]} (:body resp)]
    (do
      (reset! current-page page)
      (set! (-> "page-number" get-by-id .-value) page)
      (set! (-> "page-number" get-by-id .-max) pages)
      (set! (-> "pages-count" get-by-id .-innerHTML) pages)
      (set! (-> "records-count" get-by-id .-innerHTML) recs-count))
    (dom/render #(table/create id model data) table-panel)))

(defn get-table-id
  []
  (-> "table-panel"
      get-by-id
      .-firstChild
      .-id))

(defn page-number-changed
  [event]
  (let [temp-p-num (-> event .-target .-value)
        pages-count (read-string (-> (get-by-id "pages-count") .-innerHTML))
        p-num (if (> temp-p-num pages-count)
                  pages-count
                  temp-p-num)
        table-id (get-table-id)]
    (reset! current-page p-num)
    (set! (-> event .-target .-value) p-num)
    (->
      (js/fetch (make-url
                table-id
                p-num
                (get-value "query")
                @records-limit))
      (.then #(.json %))
      (.then #(render-table (js->clj % :keywordize-keys true))))
    (add-table-event-listeners)))

(defn query-changed
  [event]
  (let [query (-> event .-target .-value)
        table-id (get-table-id)]
    (when (= "Enter" (-> event .-key))
      (reset! current-page 1)
      (set! (-> "page-number" get-by-id .-value) 1)
      (->
        (js/fetch (make-url
                   table-id
                   1
                   query
                   @records-limit))
        (.then #(.json %))
        (.then #(render-table (js->clj % :keywordize-keys true))))
      (add-table-event-listeners))))

(defn text-snippets-dragstart
  [event]
  (-> event
      .-dataTransfer
      (.setData "text/plain" (-> event .-target (.getAttribute "name")))))

(defn add-behavior
  []
  (add-event-listener "page-number" "change" page-number-changed)
  (add-event-listener "query" "keydown" query-changed)
  (doall
    (for [el (get-by-class "text-snippets")]
         (do
           (.addEventListener el "dragstart" text-snippets-dragstart))))
  (add-table-event-listeners)
  (add-context-menu-event-listener))

(add-behavior)

(comment

(.open js/window (make-url
                   "verifications"
                   p-num
                   (get-value "query")
                   @records-limit))

)
