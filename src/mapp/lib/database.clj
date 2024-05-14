(ns metrology.lib.database
  (:require 
    [clojure.java.jdbc :as jdbc]
    [clojure.string]))

(defmacro defdb
  [s]
  `(def ~s
    {:classname "org.sqlite.jdbc"
     :subprotocol "sqlite"
     :subname ~(str "data/" s ".db")}))

(defdb personal)

(defdb tasks)

(defn active
  "Активные задачи."
  [db]
  (jdbc/query
    db
    ["select * from tasks
      where 
        (status >= 0)
        and (status < 100)
      order by priority;"]))

(defn prepare-val
  "Оборачивает строки в кавычки, nil заменяет на null."
  [el]
  (cond
    (= el nil)
      "null"
    (= (class el) java.lang.String)
      (str \" el \")
    (= (class el) clojure.lang.Keyword)
      (replace (str \" el \") ":" "")
    :else
      el))

(defn prepare-vals
  [& xs]
  (clojure.string/join 
    ", "
    (map prepare-val xs)))

(defn clear-record
  "Все значения хэша меняются на nil, либо значения ключей переданных вторым
   аргументом."
  ([m]
   (apply hash-map (flatten (map (fn [k] (list k nil)) (keys m)))))
  ([m coll]
   (reduce (fn [a b] (assoc a b nil)) m coll)))

(defn new-record
  "Возвращает пустую запись выбранной таблицы БД."
  [db table]
  (->>
    (str "select * from " table " limit 1;")
    (jdbc/query db)
    first
    clear-record))

(comment

(def record (atom nil))

(reduce (fn [a b] (assoc a b nil)) @record '(:channels :sw_name :protocol))

(clear-record @record '(:channels :sw_name))

@record

(some (fn [x] (= x :sw_name)) (keys @record))

(reset! record (new-record midb "conditions"))

(reset! record (first (jdbc/query midb "Select * from verification limit 1;")))

(swap! record clear-record)

(clear-record @record)

(.toString (java.time.LocalDate/now))

(assoc {:key1 1.2 :key2 32 :key3 200} :key1 5.4)

(conj {} {:key1 1 :key2 2 :key3 3} {:key4 4})

(assoc-in {:key1 1.2 :key2 32 :key3 200} [:key1] (5.4 5.4))

(active tasks)

;; documentations

(require '[clojure.repl :refer :all])

(find-doc "hash-map")

(doc jdbc/execute!)

(dir clojure.string)

(macroexpand '(defdb midb))

)
