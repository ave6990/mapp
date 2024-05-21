(ns cljs.core
  (:require
    [clojure.string :as string]
    [reagent.core :as r]
    [reagent.dom :as dom]
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

(defn get-by-id
  [id]
  (.getElementById js/document id))

(defn get-by-class
  [class-name]
  (seq (.getElementsByClassName js/document class-name)))

(defn get-by-tag
  [tag-name]
  (seq (.getElementsByTagName js/document tag-name)))

(defn get-value
  [id]
  (-> (get-by-id id)
      .-value))

(defn add-event-listener
  [id event fn]
  (.addEventListener (get-by-id id) event fn))

;;#table#event#listener
(defn td-clicked
  [event]
  (let [tr (-> event .-target .-parentNode)]
    (if (-> tr .-classList (.contains "selected"))
        (-> tr .-classList (.remove "selected"))
        (-> tr .-classList (.add "selected")))))

(defn add-table-event-listeners
  []
  (doall
    (for [el (get-by-tag "td")]
         (do
           (.addEventListener el "click" td-clicked)))))

(defn create-table-header
  [model]
  [:tr
    (for [[id nm _] model]
         [(keyword (str "th.col" (keyword id)))
           nm])])

(defn create-table-row
  [model row-data]
  (let [ids (for [[id _ _] model] id) 
        editables (for [[_ _ editable] model] (str editable))]
    [:tr 
      (map (fn [id editable]
               [(keyword (str "td.col" (keyword id)))
                    {:contenteditable editable}
                 ((keyword id) row-data)])
           ids
           editables)]))

(defn create-table-rows
  [model data]
  (map (fn [row]
           (create-table-row model row))
       data))

(defn create-table
  "args: model [[id name visibility] [] ... []]"
  [id model data]
  [:table {:id id}
    [:thead
      (create-table-header model)]
    [:tbody
      (create-table-rows model data)]])

(def html-tab
  [:table [:thead [:tr [:th "col_1"] [:th "col_2"]]] [:tbody [:tr [:td 1] [:td 2]]]])

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
    (dom/render #(create-table id model data) table-panel)))

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
      (.log js/console query)
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
  (add-table-event-listeners))

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
