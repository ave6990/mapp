(ns cljs.table-handlers
  (:require
    [clojure.string :as string]
    [cljs.dom-functions :refer :all]
    [cljs.context-menu :as ctx-menu]))

(defn toggle-select-row
  [tr]
  (toggle-class tr "selected"))

(defn unselect-rows
  []
  (doall
    (for [el (get-by-tag "tr")]
         (remove-class el "selected"))))

(defn select-all-rows
  []
  (doall
    (for [el (get-by-tag "tr")]
         (when (not (contains-class el "headers-row"))
               (add-class el "selected")))))

(defn get-row-number
  [tr]
  (when tr
    (-> tr
        (.getAttribute "id")
        (string/replace #"row_" "")
        js/parseInt)))

(defn select-row-by-number
  [n]
  (-> "row_"
      (str n)
      get-by-id
      (add-class "selected")))

(defn select-rows-group
  [event]
  (let [id (-> event
               .-target
               .-parentNode
               get-row-number)
        last-id (-> "selected"
                  get-by-class
                  last
                  get-row-number)
        direction (if (> id last-id) 1 -1)]
    (if last-id
        (dorun
          (for [i (range last-id (+ id direction) direction)]
               (select-row-by-number i)))
        (select-row-by-number id))))

(defn td-click
  [event]
  (when (= "TD" (-> event .-target .-tagName))
    (let [tr (-> event .-target .-parentNode)]
      (ctx-menu/hide)
      (when (-> event .-ctrlKey)
            (toggle-select-row tr))
      (when (-> event .-shiftKey)
            (select-rows-group event)))))

(defn td-change
  [event]
  (println "****")
  (.log js/console event))

(defn add-event-listeners
  []
  (let [el (get-by-id "table-panel")
        observer (js/MutationObserver. td-change)]
    (-> observer (.observe el (clj->js {:characterData true})))  ;; don't work
    (.addEventListener el "click" td-click)
    (.addEventListener el "contextmenu" ctx-menu/show)))
