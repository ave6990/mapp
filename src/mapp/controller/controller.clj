(ns mapp.controller.controller
  (:require
    [clojure.string :as string]
    [net.cgrand.enlive-html :as html]
    [hiccup2.core :as h]
    [mapp.model.midb :as midb]
    [mapp.views.view :as v]
    [mapp.views.templates :as tmpl]
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

(defn parse-request
  [req get-fn]
  (let [params (:params req)
        limit (read-string (:limit params))
        query (:q params)
        offset (calc-offset (read-string (:id params)) limit)
        recs (get-fn query limit offset)
        res (if recs
                recs
                {:count 0 :data (repeat (vs/fields-settings) " ")})
        pages (calc-pages (:count res) limit)]
    {:query query
     :limit limit
     :offset offset
     :data (:data res)
     :count (:count res)
     :pages pages}))

(defn get-verifications
  [req]
  (let [params (:params req)
        limit (read-string (:limit params))
        query (:q params)
        offset (calc-offset (read-string (:id params)) limit)
        recs (midb/get-verifications query limit offset)
        res (if recs
                recs
                {:count 0 :data (repeat (vs/fields-settings) " ")})]
    (str
      (h/html
        (tmpl/gen-page
          "Журнал ПР"
          (tmpl/page-template
            nil
            (tmpl/query-panel
              (calc-pages (:count res) limit)
              (:count res))
            (tmpl/create-table
              "verifications"
              vs/fields-settings
              (:data res))
            nil))))))

(defn get-verifications-table-panel
  [req]
  (let [data (parse-request req midb/get-verifications)]
    {:status 200
     :body "hello" #_{:pages (calc-pages (:count data) (:limit data))
            :count (:count data)
            :table
              (->
                (tmpl/create-table
                  "verifications"
                  vs/fields-settings
                  (:data data))
                h/html
                str)}}))
