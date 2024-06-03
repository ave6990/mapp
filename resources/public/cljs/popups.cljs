(ns cljs.popups
  (:require
    [clojure.string :as string]
    [cljs.dom-functions :refer :all]
    [cljs.table :as table]
    [cljs.table-handlers :as table-handlers]
    [cljs.query-panel-handlers :as query-handler])) ;;TOFIX for autorefreshing page after data changing

(defn popup-no-click
  [event]
  (dorun
    (for [popup (get-by-class "popup")]
         (remove-class popup "show-popup"))))

(defn save-popup-yes-click
  [event]
  (let [records (table/read-selected-rows)
        tab-id (table/get-table-id)]
    (table-handlers/unselect-rows)
    (remove-class (get-by-id "save-popup") "show-popup")
    (js/fetch (str "/" tab-id "/save")
      (clj->js {:method "POST"
                :headers {"Content-type" "application/json;charset=utf-8"}
                :body (stringify {:data records
                                  :table tab-id})}))))

(defn copy-popup-yes-click
  [event]
  (let [rec-num (read-string (get-html "copy-record-number"))
        cnt (read-string (get-value "copy-count"))
        tab-id (table/get-table-id)]
    (.log js/console "Copy record " rec-num cnt " times")
    (table-handlers/unselect-rows)
    (remove-class (get-by-id "copy-popup") "show-popup")
    (js/fetch (str "/" tab-id "/copy")
      (clj->js {:method "POST"
                :headers {"Content-type" "application/json;charset=utf-8"}
                :body (stringify {:id rec-num
                                  :cnt cnt
                                  :table tab-id})}))))

(defn delete-popup-yes-click
  [event]
  (let [data (table/read-selected-rows)
        ids (map :id data)
        rec-nums (map read-string ids)
        tab-id (table/get-table-id)]
    (table-handlers/unselect-rows)
    (remove-class (get-by-id "delete-popup") "show-popup")
    (println ids)
    (js/fetch (str "/" tab-id "/delete")
      (clj->js {:method "DELETE"
                :headers {"Content-type" "application/json;charset=utf-8"}
                :body (stringify {:table tab-id
                                  :ids rec-nums
                                  :data data})}))))

(defn gen-value-popup-yes-click
  [event]
  (let [data (table/read-selected-rows)
        ids (map :id data)
        rec-nums (map read-string ids)]
    (table-handlers/unselect-rows)
    (remove-class (get-by-id "gen-value-popup") "show-popup")
    (js/fetch (str "/verifications/gen-value")
      (clj->js {:method "POST"
                :headers {"Content-type" "application/json;charset=utf-8"}
                :body (stringify {:ids rec-nums})}))))

(defn add-popup-event-listeners
  []
  (doall
    (for [but (get-by-class "popup-no")]
         (.addEventListener but "click" popup-no-click)))
  (.addEventListener (get-by-id "copy-popup-yes") "click" copy-popup-yes-click)
  (.addEventListener (get-by-id "delete-popup-yes") "click" delete-popup-yes-click)
  (.addEventListener (get-by-id "save-popup-yes") "click" save-popup-yes-click)
  (.addEventListener (get-by-id "gen-value-popup-yes") "click" gen-value-popup-yes-click))


