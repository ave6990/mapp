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
    [mapp.views.operations-settings :as ops]
    [mapp.views.verification-operations-settings :as v-ops]
    [mapp.views.refs-set-settings :as refs-set]
    [mapp.views.measurements-settings :as meas]
    [mapp.views.channels-settings :as ch]
    [mapp.views.metrology-settings :as metr]
    [mapp.views.conditions-settings :as cs]
    [mapp.utils.protocol :as pr]))

(defn keywordize
  "Функция преобразующая ключи входящего JSON в keyword"
  [m]
  (into {}
        (for [[k v] m]
             (do
               [(keyword k) 
                (cond
                  (vector? v)
                    (vec
                      (map (fn [vl]
                               (if (map? vl)
                                   (keywordize vl)
                                   vl))
                           v))
                  (map? v) (keywordize v)
                  :else v)]))))

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
  [title table-id params get-fn fields-settings toolbar-settings context-menu-settings]
  (let [request (parse-request params)
        {:keys [query limit offset]} request
        records (get-fn
                  query limit offset)
        {:keys [recs-count data]} records]
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
              query
              (calc-pages recs-count limit)
              recs-count)
            (tmpl/create-table
              table-id
              fields-settings
              data)
            nil
            (tmpl/context-menu
              context-menu-settings)))))))

(defmacro make-get-page
  [title table-id fields-settings toolbar-settings context-menu-settings]
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
           ~toolbar-settings
           ~context-menu-settings)))))

(make-get-page "Журнал ПР" "verifications" vs/fields-settings vs/toolbar-fields-settings vs/context-menu-settings)
(make-get-page "Условия поверки" "conditions" cs/fields-settings cs/toolbar-fields-settings [])
(make-get-page "Эталоны" "references" refs/fields-settings refs/toolbar-fields-settings [])
(make-get-page "ГСО" "gso" gso/fields-settings gso/toolbar-fields-settings [])
(make-get-page "Контрагенты" "counteragents" ca/fields-settings ca/toolbar-fields-settings [])
(make-get-page "МП" "methodology" met/fields-settings met/toolbar-fields-settings met/context-menu-settings)
(make-get-page "Операции поверки" "v-operations" ops/fields-settings ops/toolbar-fields-settings [])
(make-get-page "Операции поверки по НД" "verification-operations" v-ops/fields-settings v-ops/toolbar-fields-settings [])
(make-get-page "КСП" "refs-set" refs-set/fields-settings refs-set/toolbar-fields-settings [])
(make-get-page "Результаты измерений" "measurements" meas/fields-settings meas/toolbar-fields-settings [])
(make-get-page "Каналы измерений" "channels" ch/fields-settings ch/toolbar-fields-settings ch/context-menu-settings)
(make-get-page "Метрологические характеристики" "metrology" metr/fields-settings metr/toolbar-fields-settings [])

(defn get-protocols
  [req]
  (let [data (midb/get-protocols-data (:q req))]
    (pr/protocols data)))

(defn get-verifications-data
  [req]
  (get-data req midb/get-verifications vs/fields-settings))

(defn gen-value-verifications
  [body]
  (let [ids (:ids (keywordize body))
        query (str "id = " 
                   (string/join " or id = " ids))
        protocols-data (seq (midb/get-protocols-data query))]
    (println "gen-values! for records" query)
    (midb/gen-values! protocols-data)))

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

(defn get-operations-data
  [req]
  (get-data req midb/get-v-operations ops/fields-settings))

(defn get-channels-data
  [req]
  (get-data req midb/get-channels ch/fields-settings))

(defn get-metrology-data
  [req]
  (get-data req midb/get-metrology metr/fields-settings))

(defn get-refs-set-data
  [req]
  (get-data req midb/get-refs-set refs-set/fields-settings))

(defn get-verification-operations-data
  [req]
  (get-data req midb/get-verification-operations v-ops/fields-settings))

(defn get-measurements-data
  [req]
  (get-data req midb/get-measurements meas/fields-settings))

(defn write-verifications
  [body]
  (let [data (-> body keywordize :data)]
    (dorun
      (for [rec data]
           (midb/write!
             :verification
             rec)))
    (println "Save verification records complete! ")))

(defn filter-refs
  [data type-id]
  (filter (fn [rec]
              (= type-id (:type_id rec)))
          data))

(defn change-field
  [data k]
  (map (fn [{:keys [id v_id ref_id]}]
           {:id id :v_id v_id k ref_id})
       data))

(defn prepare-data
  [data type-id field]
  (-> data
      (filter-refs type-id)
      (change-field field)))

(defn write-gso
  [data]
  (midb/write-multi!
    :v_gso
    data))

(defn write-refs
  [data]
  (midb/write-multi!
    :v_refs
    data))

(defn write-opt-refs
  [data]
  (midb/write-multi!
    :v_opt_refs
    data))

(defn write-refs-set
  [body]
  (let [data (-> body keywordize :data)
        gso (prepare-data data "3" :gso_id)
        refs (prepare-data data "1" :ref_id)
        opt-refs (prepare-data data "2" :ref_id)]
    (when (not (empty? gso))
          (write-gso gso))
    (when (not (empty? refs))
          (write-refs refs))
    (when (not (empty? opt-refs))
          (write-opt-refs opt-refs))))

(defn copy-refs-set
  [body]
  ())

(defn delete-refs-set
  [body]
  ())

(defn copy-verifications
  [body]
  (let [{:keys [id cnt]} (keywordize body)]
    (midb/copy-record! id
                       cnt)
    (println "Copy verification record complete! " id cnt)))

(defn delete-verifications
  [body]
  (let [ids (:ids (keywordize body))]
    (dorun
      (for [id ids]
           (midb/delete-record! id)))
    (println "Delete verification records compplete! " ids)))

(defn copy-refs-set ;;TODO
  [body]
  ())

(defn delete-refs-set ;;TODO
  [body]
  ())

(defn save-records
  [body]
  (let [{:keys [table data]} (keywordize body)]
    (dorun
      (for [rec data]
        (midb/write!
          (keyword (string/replace table #"-" "_"))
          rec)))
        (println (str "Save data to " table " table complete"))))

(defn copy-record
  [body]
  (let [{:keys [table id cnt]} (keywordize body)]
    (dorun
      (for [_ (range cnt)]
           (midb/copy!
             (keyword (string/replace table #"-" "_"))
             id)))
    (println (str "Copy data to " table " table complete"))))

(defn delete-records
  [body]
  (let [{:keys [table ids]} (keywordize body)]
    (dorun
      (for [id ids]
           (midb/delete!
             (keyword (string/replace table #"-" "_"))
             id)))
    (println (str "Delete data from " table " table complete"))))
