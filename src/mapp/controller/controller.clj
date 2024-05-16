(ns mapp.controller.controller
  (:require
    [clojure.string :as string]
    [net.cgrand.enlive-html :as html]
    [mapp.lib.gen-html :as h]
    [mapp.model.midb :as midb]
    [mapp.views.view :as v]
    [mapp.views.verifications-settings :as vs]))

(defn insert-string
  "Insert the string `s in `ss at the position `pos."
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

(defn get-verifications
  [query]
  (let [res (midb/get-verifications query 20 0)]
    (println
    (h/create-table
        "verifications"
        vs/fields-settings
        (:data res)))
    (string/replace
      v/verifications-page
      #"\{table\}"
      (h/create-table
        "verifications"
        vs/fields-settings
        (:data res)))))
