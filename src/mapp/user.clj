(ns mapp.user
  (:require 
    [clojure.java.jdbc :as jdbc]
    [clojure.string :as string]
    [clojure.pprint :refer [pprint]]
    [clojure.java.shell :refer [sh]]
    [clojure.repl :refer :all]
    [mapp.lib.database :as db]
    [mapp.lib.midb-queries :as q]
    [mapp.lib.chemistry :as ch]
    [mapp.utils.protocol :as pr]
    [mapp.lib.gs2000 :as gs]
    [mapp.lib.metrology :as metr]
    [mapp.views.report :as report]
    [mapp.lib.gen-html :refer :all]
    [mapp.protocols.custom :as protocol]))

(def midb-path
  ;"/mnt/d/UserData/YandexDisk/Ermolaev/midb/"
  "/media/sf_YandexDisk/Ermolaev/midb/")

(db/defdb midb)
(db/defdb auto)

(def mmidb
  "mariaDB spec"
  {:dbtype "mysql"
   :dbname "midb"
   :host "127.0.0.1"
   :port 3306
   :user "ave"
   :password "enter"})

(defn load-icu
  ;; TOFIX не работает.
  []
  (try
    (jdbc/query
      midb
      "select load_extension('/media/sf_YandexDisk/Ermolaev/midb/icu.so');")
    (catch Exception e (print e))))

(defn last-id
  "Получить id последней записи заданной таблицы."
  [s]
  (:id (first (jdbc/query 
                midb
                (q/last-id s)))))

(defn next-id
  "Получить очередной id."
  []
  (:id (first (jdbc/query
                midb
                q/next-id))))

(defn next-protocol-number
  "Получить очередной protocol_number."
  []
  (:protocol_number (first (jdbc/query
                             midb
                             q/next-protocol-number))))

(defn insert-conditions!
  "Вставка данных условий поверки в БД."
  ([m]
    (jdbc/insert! midb :conditions m))
  ([date temp moist press volt]
    (insert-conditions! (hash-map :date date
                                  :temperature temp
                                  :humidity moist
                                  :pressure press
                                  :voltage 50 
                                  :location "ОЦСМ"))))

(defn get-conditions
  "Возвращает запись БД с условиями поверки на заданную дату."
  [date]
  (jdbc/query midb
              ["select * from conditions where date = ?" date]))

(defn conditions
  ""
  [date]
  (spit
    (str midb-path
         "conditions.html")
    (report/conditions-report (get-conditions date))))

(defn find-records
  ""
  ([s]
    (map (fn [m]
             (:id m))
         (jdbc/query
          midb
          (string/replace
            q/find-records
            "{where}"
            s)))))

(defn find-verifications
   ""
  ([s]
    (map (fn [m]
             (:id m))
         (jdbc/query
          midb
          (string/replace
            q/find-verifications
            "{where}"
            s)))))

(defn find-methodology
  "Возвращает список записей методик поверки соответствующих запросу."
  [s]
  (jdbc/query midb [q/find-methodology (str "%" s "%")]))

(defn copy-verification!
  "Копировать строку таблицы verification."
  [id]
  (jdbc/execute! midb [q/copy-verification id]))

(defn delete-verification!
  "Удалить строку таблицы verification."
  [id]
  (jdbc/delete! midb :verification ["id = ?;" id]))

(defmacro defn-delete
  [s]
  (let [id (gensym "id")]
    `(defn ~(symbol (str "delete-" s "!"))
      ~(str "Удалить строки таблицы "
            (clojure.string/replace s "-" "_")
            " соответствующие заданному v_id.")
      [~id]
      (jdbc/delete! midb 
                    ~(keyword (clojure.string/replace s "-" "_"))
                    ["v_id = ?" ~id]))))

(defn-delete v-gso)
(defn-delete v-refs)
(defn-delete v-opt-refs)
(defn-delete v-operations)
(defn-delete measurements)

(defmacro defn-copy
  [s]
  (let [id-from (gensym "id-from")
        ids-to (gensym "ids-to")
        id-to (gensym "id-to")
        f (gensym "f")
        args (gensym "args")] 
    `(defn ~(symbol (str "copy-" s "!"))
      ~(str "Копировать строки таблицы "
            s
            " соответствующие заданному v_id.")
      [~id-from ~ids-to]
      (doall
        (map (fn [~id-to]
                 (map (fn [~f ~args] (~f ~args))
                      [~(symbol (str "mapp.user/delete-" s "!"))
                        (partial jdbc/execute! midb)]
                      [~id-to
                        [~(symbol (str "q/copy-" s))
                        ~id-to
                        ~id-from]]))
          ~ids-to)))))

(defn-copy v-gso)
(defn-copy v-refs)
(defn-copy v-opt-refs)
(defn-copy v-operations)
(defn-copy measurements)

(defn copy-refs-set!
  "Копировать комплект средств поверки."
  [id ids]
  (->>
    ids
    (map
      (fn []))))

(defn delete-record!
  "Удалить запись о поверке с заданным id, вместе с данными о
   эталонах, операциях и измерениях."
   [id]
   (map (fn [f] (f id))
        (list delete-v-gso! delete-v-refs! delete-v-opt-refs!
         delete-v-operations!
         delete-measurements! delete-verification!)))

(defn copy-record!
  "Копировать запиь о поверке с данными о применяемых эталонах, операциях
   поверки и результатах измерений.
   args:
     id - целочисленный идентификатор записи в БД."
  ([id-from n]
    (map (fn [i]
          (let [id-to (inc (last-id "verification"))]
            (conj (copy-verification! id-from)
                  (map (fn [f] (f id-from (list id-to)))
                       (list copy-v-gso!
                             copy-v-refs!
                             copy-v-opt-refs!
                             copy-v-operations!
                             copy-measurements!)))))
         (range n)))
  ([id-from]
    (copy-record! id-from 1)))

(defn get-verification
  "Возвращает hash-map записи verification."
  [id]
  (first (jdbc/query midb
              ["select * from verification where id = ?" id]))) 

(defmacro defn-get
  [s s-id]
  (let [id (gensym "id")
        m (gensym "m")]
    `(defn ~(symbol (str "get-"
                         (string/replace s "_" "-")))
      [~id]
      (map (fn [~m]
               (~(keyword s-id) ~m))
           (jdbc/query
             midb
             [~(str "select "
                    s-id
                    " from "
                    s
                    " where v_id = ?")
              ~id])))))

(defn-get "v_gso" "gso_id")
(defn-get "v_refs" "ref_id")
(defn-get "v_opt_refs" "ref_id")
(defn-get "v_operations" "op_id")

(defn get-v-operations
  ""
  [id]
  (jdbc/query
    midb
    ["select op_id from v_operations where v_id = ?" id]))

(defn get-record
  "Возвращает hash-map записи о поверке."
  [id]
  (conj (hash-map
          :verification (get-verification id))
          (reduce (fn [m table]
                      (conj m
                            (hash-map
                              (keyword table)
                              (jdbc/query
                                midb
                                [(str "select * from "
                                      table
                                      " where v_id = ?;")
                                 2380]))))
                          {}
                         (list "v_gso" "v_refs" "v_opt_refs"
                          "v_operations" "measurements"))))

(defn get-protocols-data
  ""
  [where]
  (let [data (jdbc/query
                midb
                (str "select * from protocol where " where))
        measurements (jdbc/query
                        midb
                        (str "select * from view_v_measurements where " where))
        html (jdbc/query
               midb
               (str "select * from v_html where " where))]
    (map (fn [m]
             (assoc
               (assoc m
                    :measurements
                    (doall (filter (fn [r]
                                       (= (:id r) (:id m)))
                                   measurements)))
               :html
               (doall (filter (fn [r]
                                (= (:id r) (:id m)))
                              html))))
         data)))

(defn assoc-multi
  [m nm]
  (reduce (fn [a b]
            (let [[k v] b]
              (assoc a k v)))
          m
          nm))

(defn get-references-data
  [where]
  (jdbc/query
    midb
    (str "select * from refs
          where " where)))

(defn references
  ""
  [where]
  (spit
    (str midb-path "references.html")
    (report/refs-report
      (get-references-data where))))

(defn get-report-data
  ""
  [coll]
  (let [v-data (jdbc/query
                midb
                (str
                  q/report-verifications
                  "("
                  (string/join ", " coll)
                  ")
                  order by date desc, id desc"))
        measurements (jdbc/query
                      midb
                      (str "select * from view_v_measurements
                          where id in ("
                          (string/join ", " coll)
                          ")"))
        operations (jdbc/query
                    midb
                    (str
                      q/get-operations
                      "("
                      (string/join ", " coll)
                      ")"))
        refs (jdbc/query
              midb
              (str "select * from verification_refs
                      where v_id in ("
                      (string/join ", " coll)
                      ")"))]
    (map (fn [m]
             (assoc-multi m
                          {:measurements
                           (doall (filter (fn [r]
                                              (= (:id r) (:id m)))
                                          measurements))
                           :refs
                           (doall (filter (fn [r]
                                              (= (:v_id r) (:id m)))
                                          refs))
                           :operations
                           (doall (filter (fn [r]
                                              (= (:v_id r) (:id m)))
                                          operations))}))
         v-data)))

(defn get-methodology-data
  [coll]
  (let [met-data
         (jdbc/query
           midb
           (str "select * from methodology
                 where id in ("
                 (string/join
                   ", "
                   coll)
                 ")"))
        metrology-data
          (jdbc/query
            midb
            (str "select * from view_metrology
                  where id in ("
                  (string/join
                    ", "
                    coll)
                  ")"))
        operations-data
          (jdbc/query
            midb
            (str "select * from verification_operations
                  where methodology_id in ("
                  (string/join
                    ", "
                    coll)
                  ")"))]
    (map (fn [m]
             (assoc-multi m
                          {:metrology
                           (doall (filter (fn [r]
                                              (= (:id r) (:id m)))
                                          metrology-data))
                           :operations
                           (doall (filter (fn [r]
                                              (= (:methodology_id r) (:id m)))
                                          operations-data))}))
         met-data)))

(defn get-conditions-by-v-id 
  [id]
  (->>
    ["select *
      from conditions
      where id = ?"
          (->>
            ["select conditions
              from verification
              where id = ?" id]
            (jdbc/query midb)
            first
            :conditions
           )]
    (jdbc/query midb)
    first))

(conj (list 233 32 245) 140)

(defn update-record!
  [record table changes]
  (jdbc/update! midb
                table
                (assoc-multi (table record) changes)
                ["id = ?" ((comp :id table) record)]))

(defn all-refs
  [id]
  (jdbc/query midb [q/all-refs id]))

(defn check-gso
  [coll column]
  (map (fn [num]
         (first (jdbc/query midb
                     [(str "select id, number_1c, pass_number, expiration_date
                            from gso
                            where " column " = ?") num])))
       coll))

(defn set-v-gso!
  [v-id coll]
  (do (delete-v-gso! v-id)
      (jdbc/insert-multi!
        midb
        :v_gso
        (vec (map (fn [el] (hash-map :v_id v-id :gso_id el))
                  coll)))))

(defn set-v-refs!
  [v-id coll]
  (do (delete-v-refs! v-id)
      (jdbc/insert-multi!
        midb
        :v_refs
        (vec (map (fn [el] (hash-map :v_id v-id :ref_id el))
                  coll)))))

(defn set-v-opt-refs!
  [v-id coll]
  (do (delete-v-opt-refs! v-id)
      (jdbc/insert-multi!
        midb
        :v_opt_refs
        (vec (map (fn [el] (hash-map :v_id v-id :ref_id el))
                  coll)))))

(defn set-v-operations!
  [v-id coll]
  (do (delete-v-operations! v-id)
      (jdbc/insert-multi!
        midb
        :v_operations
        (vec (map (fn [el] (hash-map :v_id v-id :op_id el :result 1))
                  coll)))))

(defn ins-channel!
  "Вставить запись канала измерения и метрологических характеристик."
  [ch-obj mc-list]
  (let [ch-id
          (->>
            (jdbc/insert!
              midb
              :channels
              ch-obj)
            first
            vals
            first)]
    (map (fn [m]
             (jdbc/insert!
               midb
               :metrology
               (assoc m :channel_id ch-id)))
         mc-list)))

(defn get-operations
  "Возвращает коллекцию операций поверки по заданному v_id."
  [id]
  (jdbc/query
    midb
    ["select * from view_operations where v_id = ?" id]))

(defn gen-custom-protocols
  [data]
  (doall
    (map (fn [m]
             (when (:protocol m)
                     (jdbc/delete!
                       midb
                       :v_html
                       ["id = ?" (:id m)])
                     (jdbc/insert!
                       midb
                       :v_html
                       {:id (:id m)
                        :html ((eval (read-string (str
                                                   "protocol/"
                                                   (:protocol m))))
                                m)})))
        data)))

(defn gen-protocols
  "Генерирует протоколы поверки в файл protocol.html."
  [where]
  (let [data (get-protocols-data where)]
    (spit
      (str midb-path
           "protocol.html")
           (pr/protocols data))))

(defn gen-report
  "генерирует отчет о записях в файл report.html."
  ([coll]
   (spit
     (str midb-path
          "report.html")
     (report/verification-report (get-report-data coll))))
  ([from to]
   (gen-report (range from (inc to)))))

(defn find-counteragent
  "Возвращает список контрагентов соответствующих запросу."
  [s]
  (jdbc/query midb [q/counteragents (str "%" s "%")]))

(defn counteragents
  ""
  [s]
  (spit
    (str midb-path
         "counteragents.html")
    (report/counteragents-report (find-counteragent s))))

(defn gso
  ([where]
  (spit
    (str midb-path "gso.html")
    (report/gso-report
      (jdbc/query
        midb
        (str "select * from gso"
             (if (= "" where)
                 ""
                 (str " where " where))
             " order by available desc, components, conc")))))
  ([]
   (gso "")))

(defn methodology
  [coll]
  (spit
    (str midb-path
         "methodology.html")
    (report/methodology-report (get-methodology-data coll))))

(defn gen-values!
  "Записывает в БД случайные значения результатов измерений в пределах
   основной погрешности."
  [where]
  (map (fn [prot] 
           (map (fn [m]
                    (map (fn [r]
                             (jdbc/update!
                               midb
                               :measurements
                               {:value (:value r)}
                               ["id = ?"
                                 (:measurement_id r)]))
                         (metr/gen-values m)))
                (list (:measurements prot))))
       (get-protocols-data where)))

(defn insert-measurements
  [id ch-name coll cmnt]
  (map (fn [[m-id & ref]]
         (map (fn [r-value]
                  (jdbc/insert!
                    midb
                    :measurements
                    (hash-map
                      :v_id id
                      :channel_name ch-name
                      :metrology_id m-id
                      :ref_value r-value
                      :comment cmnt)))
         ref))
       coll))

(defn add-measurements
  [id coll]
  (map (fn [item]
           (insert-measurements
             id
             (get item 0)
             (get item 1)
             (get item 2)))
       coll))

(defn unusability
  ""
  [id op_id s]
  (jdbc/update!
    midb
    :v_operations
    {:result -1
     :unusability s}
    ["v_id = ? and op_id = ?" id op_id])
  (jdbc/update!
    midb
    :v_operations
    {:result 0}
    ["v_id = ? and op_id > ?" id op_id]))

(defn parse-int [s]
  (Integer/parseInt s))

(defn calc-references-hash
  "code-example:
    (calc-references-hash
      \"v_id >= 3000\")"
  [where]
  (->> (jdbc/query
         midb
         (str "select v_id, group_concat(ref_id, ', ') as refs
               from verification_refs
               where "
               where
               " group by v_id")) 
       (map (fn [r]
                (->> (string/split
                       (:refs r)
                       #", ")
                     (map string/trim)
                     (map parse-int)
                     sort
                     hash
                     (assoc r :refs))))
       (map (fn [r]
                (jdbc/update!
                  midb
                  :verification
                  {:hash_refs (:refs r)}
                  ["id = ?" (:v_id r)])))))

(defn gs2000
  ([gen-n gas s-conc t-conc-coll]
    (map (fn [c]
             ((gs/calculator (gs/passports gen-n))
                             gas
                             :air
                             s-conc
                             c))
         t-conc-coll))
  ([gen-n s-conc t-conc-coll]
    (map (fn [c]
             ((gs/calculator (gs/passports gen-n))
                             :air
                             s-conc
                             c))
         t-conc-coll)))

;; #split#rename#scan#protocol
(defn split-pdf
  "author: Aleksandr Ermolaev
  version: 2024-04-23
  dependency: pdftk"
  [scan-path f pages start-number]
  (let [scan-pages (->
                     (sh "identify" (str scan-path f))
                     :out
                     (string/split #"\n")
                     count)
        pages (read-string pages)
        start-number (read-string start-number)]
    (dotimes [i (/ scan-pages pages)]
      (sh "pdftk"
          (str scan-path f)
          "cat"
          (str (inc (* i pages)) "-" (+ (* i pages) pages))
          "output"
          (str scan-path "9-61-" (+ i start-number) "-2024.pdf")))
    (sh "mv"
          (str scan-path f)
          (str scan-path "trash/"))))

(defn move-backups ;; TODO:
  []
  (let [department 9
        engineer-num 61
        year 2024
        scan-path "/media/sf_SCAN/"
        local-path "./scans"]
    ()))

;; #split#rename#scan#protocol
(defn protocol-backup
  "author: Aleksandr Ermolaev
  version: 2024-04-23"
  []
  (let [scan-path "/media/sf_SCAN/"
      get-files-list
        (fn []
            (doall
              (filter (fn [s]
                          (re-matches #".*\.pdf" s))
                      (->
                        (sh "ls" scan-path)
                        :out
                        (string/split #"\n")))))]
  ;; приводим имена сканов к общему виду {start_protocol}.{pages_per_protocol}.pdf
  (dorun
    (map (fn [f] 
             (sh "mv" (str scan-path f)
                      (->
                        (str scan-path f)
                        (string/replace #"Protokol" "")
                        (string/replace #"\d{6}\.pdf" "pdf"))))
         (get-files-list)))
  ;; делим общий скан на протоколы и переименовываем их.
  (dorun
    (map (fn [f]
             (let [[start-number pages _] (string/split f #"\.")]
               (split-pdf scan-path f pages start-number)
               (println "split-pdf" (str scan-path f) pages start-number)))
         (doall
           (filter (fn [f]
                       (re-matches #"\d+\.\d\.pdf" f))
                   (get-files-list)))))))

(defn date-to-iso
  [s]
  (string/join "-"
    (->
      s
      (string/split #"\.")
      reverse)))

(comment

(dir string)

(doc string/split)

(jdbc/query
  pmidb
  "select 1 as value;")

(jdbc/query
  midb
  "select * from characteristics")

(require '[clojure.repl :refer :all])

(require '[mapp.lib.midb-queries :as q] :reload)

(require '[mapp.protocols.custom :as protocol] :reload)

;; example table-rows пример функции создания строк таблицы
(spit
  (str midb-path "temp.html")
    (html (table (mapp.protocols.custom/table-rows
            (list
              (list "Детектор" "Значение уровня шумов" "Значение дрейфа")
              (list "действительное" "допускаемое" "ед. изм."
                    "действительное" "допускаемое" "ед. изм."))
            #_(list
              (list [2 1] [1 3] [1 3])
              (list [1 1] [1 1] [1 1] [1 1] [1 1] [1 1]))
            #_th))))

(require '[mapp.lib.metrology :as metr] :reload)

(require '[mapp.utils.protocol :as pr] :reload)

(require '[mapp.lib.gen-html :refer :all] :reload)

(require '[mapp.lib.midb-queries :as q] :reload)

(require '[mapp.lib.chemistry :as ch] :reload)

(doc flatten)

(doc assoc)

)
