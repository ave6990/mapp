(ns metrology.controller.table-context-menu
  (:require
    [clojure.string :as string]
    [seesaw.core :refer :all]
    [seesaw.table :refer [table-model value-at update-at!]]
    [metrology.model.midb :as midb]
    [metrology.utils.protocol :as pr]
    [metrology.view.main :as v]))

(defn get-selection-field-values
  [field tab]
  (->>
    (selection tab {:multi? true})
    (value-at tab)
    (map field)))

(defn make-filter-string
  [field where root fr]
  (let [query-text (select fr [:#query-text])
        tab (select root [:#v-table])
        id (get-selection-field-values
             field tab)]
    (text!
      query-text
      (str
        where
        (string/join (str " or " where) id))))
  fr)

(defn gen-protocols
  "Генерирует протоколы поверки в файл protocol.html."
  [where]
  (let [data (midb/get-protocols-data where)]
    (spit
      (str midb/midb-path
           "protocol.html")
           (pr/protocols data))))

(def protocols-action
  (action
    :name "Создать протокол"
    :handler
      (fn [e]
          (let [root (to-frame e)
                tab (select root [:#v-table])
                id (get-selection-field-values
                     :id tab)
                where " id = "]
            (gen-protocols
              (str
                where
                (string/join (str " or " where) id)))))))

(defn make-delete-fn
  [ids]
  (fn [e]
      (dorun
        (map (fn [id]
                 (midb/delete-record! id))
             ids))
      (println (str "Удалены записи " (string/join ", " ids)))))

(defn make-copy-fn
  [id txt]
  (fn [e]
      (midb/copy-record! id (read-string (value txt)))
      (println (str "Запись " id " скопирована " (value txt) " раз!"))))

(defn make-copy-del-records-menu
  [tab]
  [(action
       :name "Копировать запись"
       :handler
         (fn [e]
             (->>
               (v/make-copy-dialog
                 (get-selection-field-values :id tab)
                 make-copy-fn)
               pack!
               show!)))
     (action
       :name "Удалить запись"
       :handler
         (fn [e]
             (->>
               (v/make-delete-dialog 
                 (get-selection-field-values :id tab)
                 make-delete-fn)
               pack!
               show!)))])

(defn make-window-context
  [[action-name target-frame field where]]
  (action
    :name action-name
    :handler
      (fn [e]
          (->>
            target-frame
            (make-filter-string field where (to-frame e))
            pack!
            show!))))

(defn verifications-table-menu
  [e]
  (let [root (to-frame e)
        tab (select root [:#v-table])]
    (doall
    (reduce
      conj
      (conj
        (make-copy-del-records-menu tab)
        (separator))
      (reduce
        conj
        (vec (doall (map make-window-context
             [["КСП" v/svt-frame :id " v_id = "]
              ["Методика поверки" v/methodology-frame :methodology_id " id = "]
              ["Операции поверки" v/operations-frame :id " v_op.v_id = "]
              ["Результаты измерений" v/measurements-frame :id " v.id = "]
              ["Журнал ПР" v/journal-frame :id " id = "]])))
        [(separator)
        protocols-action])))))

;;TOFIX delete records from verification table
#_(defn conditions-table-menu
  [e]
  (let [tab (select (to-root e) [:#v-table])]
    (make-copy-del-records-menu tab)))

(comment

(ns metrology.controller.table-context-menu)
(require '[metrology.model.midb :as midb] :reload)
(require '[metrology.view.main :as main] :reload)
(require '[metrology.utils.protocol :as pr] :reload)

)
