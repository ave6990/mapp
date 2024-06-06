(ns cljs.query-panel-handlers
  (:require
    [reagent.core :as r]
    [reagent.dom :as dom]
    [cljs.config :as cnfg]
    [cljs.dom-functions :refer :all]
    [cljs.table :as table]
    [cljs.table-handlers :as table-handlers]))

(defn make-url
  [section page query limit]
  (str cnfg/site section 
       "?q=" query
       "&page=" page
       "&limit=" limit))

(defn render-table
  [resp]
  (let [table-panel (get-by-id "table-panel")
        id (-> table-panel .-firstChild .-id)
        {:keys [model data recs-count pages page]} (:body resp)]
    (do
      (set! (-> "page-number" get-by-id .-value) page)
      (set! (-> "page-number" get-by-id .-max) pages)
      (set! (-> "pages-count" get-by-id .-innerHTML) pages)
      (set! (-> "records-count" get-by-id .-innerHTML) recs-count))
    (dom/render #(table/create id model data) table-panel)
    (table-handlers/add-event-listeners)))

(defn execute-query
  []
  (let [table-id (table/get-table-id)
        query (get-value "query")
        page-number (js/parseInt
                      (get-value "page-number"))]
    (println table-id query page-number)
    (->
      (js/fetch (make-url
                  (str table-id "/get")
                  page-number
                  query
                  @cnfg/records-limit))
      (.then #(.json %))
      (.then #(render-table (js->clj % :keywordize-keys true))))))

(defn page-number-changed
  [event]
  (let [temp-p-num (-> event .-target .-value)
        pages-count (js/parseInt (-> (get-by-id "pages-count") .-innerHTML))
        p-num (if (> temp-p-num pages-count)
                  pages-count
                  temp-p-num)]
    (set! (-> event .-target .-value) p-num)
    (execute-query)))

(defn query-changed
  [event]
  (let [query (-> event .-target .-value)]
    (when (= "Enter" (-> event .-key))
      (set! (-> "page-number" get-by-id .-value) 1)
      (execute-query))))

(defn add-event-listeners
  []
  (add-event-listener "page-number" "change" page-number-changed)
  (add-event-listener "query" "keydown" query-changed))
