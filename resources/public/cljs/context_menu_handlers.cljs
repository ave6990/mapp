(ns cljs.context-menu-handlers
  (:require
    [clojure.string :as string]
    [cljs.dom-functions :refer :all]
    [cljs.config :as cnfg]
    [cljs.table :as table]
    [cljs.table-handlers :as table-handlers]
    [cljs.context-menu :as ctx-menu]))

(defn make-filter-query
  [where ids]
  (str where
       (string/join (str " or " where) ids)))

(defn make-url
  [section where ids]
  (str cnfg/site section
       "?q=" (make-filter-query where ids)
       "&page=1&limit=" @cnfg/records-limit))

(defn ctx-action-unselect
  [event]
  (table-handlers/unselect-rows))

(defn ctx-action-select-all
  [event]
  (table-handlers/select-all-rows))

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

(defn ctx-action-methodology
  [event]
  (let [ids (->> (table/read-selected-rows) (map :methodology_id) set)]
  ;(let [data (table/read-selected-rows)
  ;      ids (set (map :methodology_id data))]
    (.open js/window
      (make-url "methodology" "id = " ids))))

(defn ctx-action-operations
  [event]
  (let [ids (->> (table/read-selected-rows) (map :methodology_id) set)]
    (.open js/window
      (make-url "verification-operations" " methodology_id = " ids))))

(defn ctx-action-journal
  [event]
  (let [ids (->> (table/read-selected-rows) (map :id) set)]
    (->
      (js/fetch
        (make-url
          "journal/get" " id = " ids))
      (.then #(.blob %))
      (.then  #(let [a (.createElement js/document "a")]
                  (.log js/console a)
                  (set! (.-href a) (.createObjectURL (.-URL js/window) %)) 
                  (set! (.-download a) "journal.xls")
                  (.log js/console a)
                  (.click a))))))

(defn ctx-action-refs-set
  [event]
  (let [ids (->> (table/read-selected-rows) (map :id) set)]
    (.open js/window
      (make-url "refs-set" "v_id = " ids))))

(defn ctx-action-measurements
  [event]
  (let [ids (->> (table/read-selected-rows) (map :id) set)]
    (.open js/window
      (make-url "measurements" "v_id = " ids))))

(defn ctx-action-channels
  [event]
  (let [field (case (table/get-table-id)
                "methodology" :id
                "verifications" :methodology_id)
        ids (->> (table/read-selected-rows) (map field) set)]
    (.open js/window
      (make-url "channels" "methodology_id = " ids))))

(defn ctx-action-metrology
  [event]
  (let [ids (->> (table/read-selected-rows) (map :id) set)]
    (.open js/window
      (make-url "metrology" "ch.id = " ids))))

(defn ctx-action-gen-value
  [event]
  (add-class (get-by-id "gen-value-popup") "show-popup"))

(defn ctx-action-protocols
  [event]
  (let [ids (->> (table/read-selected-rows) (map :id) set)]
    (.open js/window
      (make-url "protocols" " id = " ids))))

(defn ctx-action-verification-operations
  [event]
  (let [ids (->> (table/read-selected-rows) (map :id) set)]
    (.open js/window
      (make-url "verification-operations" " methodology_id = " ids))))

(defn ctx-action-ref-values
  [event]
  (let [rows (table/read-selected-rows)
        where (case (table/get-table-id)
                "measurements"
                  {:field " rv.metrology_id = "
                   :ids (->> rows (map :metrology_id) set)}
                "channels"
                  {:field " ch.id = "
                   :ids (->> rows (map :id) set)})]
    (.open js/window
      (make-url "ref-values" (:field where) (:ids where)))))

(def menu-actions
  {"ctx-menu-action-save" ctx-action-save
   "ctx-menu-action-copy" ctx-action-copy
   "ctx-menu-action-delete" ctx-action-delete
   "ctx-menu-action-unselect" ctx-action-unselect
   "ctx-menu-action-select-all" ctx-action-select-all
   "ctx-menu-action-methodology" ctx-action-methodology
   "ctx-menu-action-operations" ctx-action-operations
   "ctx-menu-action-measurements" ctx-action-measurements
   "ctx-menu-action-channels" ctx-action-channels
   "ctx-menu-action-metrology" ctx-action-metrology
   "ctx-menu-action-refs-set" ctx-action-refs-set
   "ctx-menu-action-gen-value" ctx-action-gen-value
   "ctx-menu-action-verification-operations" ctx-action-verification-operations
   "ctx-menu-action-ref-values" ctx-action-ref-values
   "ctx-menu-action-journal" ctx-action-journal
   "ctx-menu-action-protocols" ctx-action-protocols})

(defn add-event-listeners
  []
  (doall
    (for [el (get-by-class "context-menu-item")]
         (.addEventListener el "click" (ctx-menu/item-click menu-actions)))))
