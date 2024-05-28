(ns cljs.table-handlers
  (:require
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

(defn td-click
  [event]
  (when (= "TD" (-> event .-target .-tagName))
    (let [tr (-> event .-target .-parentNode)]
      (when (-> event .-ctrlKey)
            (toggle-select-row tr))
      (ctx-menu/hide))))

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
