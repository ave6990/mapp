(ns mapp.controller.controller
  (:require
    [clojure.string :as string]
    [mapp.model.midb :as midb]
    [mapp.views.view :as v]))

(defn insert-string
  [ss s pos]
  (string/replace
    (->>
      (list 
        (take pos ss)
        s
        (drop pos ss))
      (map string/join)
      string/join)
    #"/s+"
    " "))

(defn calc-offset
  [page limit]
  (* (dec page) limit))

(defn calc-pages
  [records-count limit]
  (int (Math/ceil (/ records-count limit))))

(defn make-write-fn
  [id data]
  (fn [e]
      (midb/write!
        id
        data)))
