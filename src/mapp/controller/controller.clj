(ns mapp.controller.controller
  (:require
    [clojure.string :as string]
    [clojure.pprint :refer [pprint]]
    [net.cgrand.enlive-html :as html] ;;DELETE not used
    [hiccup2.core :as h]
    [mapp.model.midb :as midb]
    [mapp.views.view :as v]
    [mapp.views.templates :as tmpl]
    [mapp.views.verifications-settings :as vs]
    [mapp.views.gso-settings :as gso]
    [mapp.views.references-settings :as refs]
    [mapp.views.counteragents-settings :as ca]
    [mapp.views.methodology-settings :as met]
    [mapp.views.conditions-settings :as cs]))

(defn keywordize
  [m]
  (into {}
        (for [[k v] m]
             [(keyword k) v])))

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
  [params]
  (let [limit (read-string (:limit params))
        query (:q params)
        page (read-string (:page params))
        offset (calc-offset page limit)]
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
  [params get-fn fields-settings]
  (let [request (parse-request params)
        {:keys [query limit offset]} request
        records (get-fn
                  (string/replace query #"\*" "%") limit offset)
        {:keys [recs-count data]} records]
    {:status 200
     :body (merge request
                  {:recs-count recs-count
                   :pages (calc-pages recs-count limit)
                   :data data
                   :model fields-settings})}))

(defn get-page
  [title table-id params get-fn fields-settings toolbar-settings]
  (let [request (parse-request params)
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
            (list
              (tmpl/toolbar-text-snippets
                tmpl/symbols)
              (tmpl/toolbar-text-snippets
                toolbar-settings))
            (tmpl/query-panel
              (calc-pages recs-count limit)
              recs-count)
            (tmpl/create-table
              table-id
              fields-settings
              data)
            nil))))))

(defmacro make-get-page
  [title table-id fields-settings toolbar-settings]
  (let [req (gensym "req")
        params (gensym "params")]
    `(defn ~(symbol (str "get-" table-id "-page"))
       [~req]
       (let [~params (if (zero? (count ~req))
                     {:page "1"
                      :q ""
                      :limit "100"}
                     ~req)]
         (mapp.controller.controller/get-page
           ~title
           ~table-id
           ~params
           ~(symbol (str "midb/get-" table-id))
           ~fields-settings
           ~toolbar-settings)))))

(make-get-page "Журнал ПР" "verifications" vs/fields-settings vs/toolbar-fields-settings)
(make-get-page "Условия поверки" "conditions" cs/fields-settings cs/toolbar-fields-settings)
(make-get-page "Эталоны" "references" refs/fields-settings refs/toolbar-fields-settings)
(make-get-page "ГСО" "gso" gso/fields-settings gso/toolbar-fields-settings)
(make-get-page "Контрагенты" "counteragents" ca/fields-settings ca/toolbar-fields-settings)
(make-get-page "МП" "methodology" met/fields-settings met/toolbar-fields-settings)

(defn get-verifications-data
  [req]
  (get-data req midb/get-verifications vs/fields-settings))

(defn get-conditions-data
  [req]
  (get-data req midb/get-conditions cs/fields-settings))

(defn get-gso-data
  [req]
  (get-data req midb/get-gso gso/fields-settings))

(defn get-references-data
  [req]
  (get-data req midb/get-references refs/fields-settings))

(defn get-counteragents-data
  [req]
  (get-data req midb/get-counteragents ca/fields-settings))

(defn get-methodology-data
  [req]
  (get-data req midb/get-methodology met/fields-settings))

(defn write-verifications
  [body]
  (pprint body))

(defn copy-verifications
  [body]
  (let [{:keys [id cnt]} (keywordize body)]
    (midb/copy-record! id
                       cnt)
    (println "Copy complete! " id cnt)))

(defn delete-verifications
  [body]
  (pprint body))
