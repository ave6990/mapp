(ns mapp.controller.controller
  (:require
    [clojure.string :as string]
    [net.cgrand.enlive-html :as html]
    [hiccup2.core :as h]
    [mapp.model.midb :as midb]
    [mapp.views.view :as v]
    [mapp.views.templates :as tmpl]
    [mapp.views.verifications-settings :as vs]
    [mapp.views.gso-settings :as gso]
    [mapp.views.references-settings :as refs]
    [mapp.views.counteragents-settings :as ca]
    [mapp.views.conditions-settings :as cs]))

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
  [req]
  (let [params (:params req)
        limit (read-string (:limit params))
        query (:q params)
        page (read-string (:id params))
        offset (calc-offset page limit)]
    (println req)
    {:query query
     :limit limit
     :offset offset
     :page page}))

(defn get-empty-record
  [fields-settings]
  [(reduce (fn [m [k _]] (merge m {k " "}))
          {}
          fields-settings)])

(defn get-data
  [req get-fn fields-settings]
  (let [request (parse-request req)
        b (println "###" request)
        {:keys [query limit offset]} request
        a (println "###" request)
        records (get-fn
                  query limit offset)
        l (println "### " records)
        {:keys [recs-count data]} records]
    {:status 200
     :body (merge request
                  {:recs-count recs-count
                   :pages (calc-pages recs-count limit)
                   :data data
                   :model fields-settings})}))

(defn get-page
  [title table-id req get-fn fields-settings]
  (let [request (parse-request req)
        {:keys [query limit offset]} request
        records (get-fn
                  query limit offset)
        {:keys [recs-count data]} records
        ]
    (str
      (h/html
        (tmpl/gen-page
          title
          (tmpl/page-template
            nil
            (tmpl/query-panel
              (calc-pages recs-count limit)
              recs-count)
            (tmpl/create-table
              table-id
              fields-settings
              data)
            nil))))))

(defmacro make-get-page
  [title table-id fields-settings]
  (let [req (gensym "req")]
    `(defn ~(symbol (str "get-" table-id "-page"))
       [~req]
       (mapp.controller.controller/get-page
         ~title
         ~table-id
         ~req
         ~(symbol (str "midb/get-" table-id))
         ~fields-settings))))

(make-get-page "Журнал ПР" "verifications" vs/fields-settings)
(make-get-page "Условия поверки" "conditions" cs/fields-settings)
#_(make-get-page "Эталоны" "references" refs/fields-settings)
#_(make-get-page "ГСО" "gso" gso/fields-settings)
#_(make-get-page "Контрагенты" "counteragents" ca/fields-settings)

(defn get-verifications-data
  [req]
  (get-data req midb/get-verifications vs/fields-settings))

(defn get-conditions-data
  [req]
  (get-data req midb/get-conditions cs/fields-settings))
