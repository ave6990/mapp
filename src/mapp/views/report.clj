(ns mapp.views.report
  (:require [mapp.lib.gen-html :refer :all]
            [mapp.utils.protocol :as pr]
            [mapp.lib.metrology :as metr]
            [clojure.string :as string]))

(def styles
"html {
  color: #393939;
  font: 10pt sans-serif;
}
table {
  border: 1px solid lightgray;
  border-collapse: collapse;
  margin: 4pt 0;
}
th, td {
  padding: 0 3pt;
  border: 1px solid lightgray;
}
hr {
  border-color: darkblue;
}")

(def gso-styles
"html {
  color: #393939;
  font: 10pt sans-serif;
}
table {
  border: 1px solid lightgray;
  border-collapse: collapse;
  margin: 4pt 0;
  width: 100%;
}
th, td {
  max-width: 100px;
  padding: 0 3pt;
  border: 1px solid lightgray;
}")

(def scripts
  "Обновляет страницу при возвращении фокуса."
  "var blurred = false
  window.addEventListener('blur', (e) => {
    blurred = true
  })
  window.addEventListener('focus', (e) => {
    if (blurred) {
      location.reload()
    }
  })")

(defn report
  ([page st sc content]
   (doctype
     (html
       (head
         (meta {:charset "utf-8"})
         (meta {:name "author" :content "Aleksandr Ermolaev"})
         (meta {:name "e-mail" :content "ave6990@ya.ru"})
         (meta {:name "version" :content "2023-09-21"})
         (title page)
         (style {:type "text/css"} st)
         (script #_{:type "text/javascript"} #_sc))
       content)))
  ([page st content]
   (report page st "" content))
  ([page content]
   (report page "" "" content)))

(defn gen-th
  [coll]
  (string/join
    "\n"
    (map (fn [s]
             (th s))
         coll)))

(defn common-data
  [m]
  (div
    (p "Общие сведения")
    (table
      (thead
        (tr
          (gen-th (list "id" "№ протокола" "счет" "дата"
                        "вид поверки" "скопировано"))))
      (tbody
        (tr
          (td (:id m))
          (td (:protocol_number m))
          (td (:count m))
          (td (:date m))
          (td (if (= 1 (:verification_type m))
                  "периодическая"
                  "первичная"))
          (td (:copy_from m)))
        (tr
          (th {:colspan 6}
              "поверитель"))
        (tr
          (td (:engineer m))
          (td {:colspan 3}
              (string/join
                " " 
                (list (:last_name m)
                      (:first_name m)
                      (:second_name m))))
          (td {:colspan 2} (:comment m)))))))

(defn counteragent
  [m]
  (div
    (p "Собственник")
    (table
      (tr
        (th "id")
        (td (:counteragent_id m))
        (th "ИНН")
        (td (:inn m)))
      (tr
        (th {:colspan 2} "наименование")
        (td {:colspan 2} (:counteragent_name m)))
      (tr
        (th {:colspan 2} "сокращенное")
        (td {:colspan 2} (:counteragent_short_name m)))
      (tr
        (th {:colspan 2} "адрес")
        (td {:colspan 2} (:address m))))))

(defn mi-data
  [m]
  (div
    (p "Сведения о СИ")
    (table
      (tr
        (th "тип СИ")
        (td {:colspan 4} (:mi_type m)))
      (tr
        (gen-th (list "рег. №" "год изг." "зав. №"
                      "сфера" "МПИ")))
      (tr
        (td (:registry_number m))
        (td (:manufacture_year m))
        (td (:serial_number m))
        (td (:area m))
        (td (:interval m)))
      (tr
        (th "каналов")
        (th {:colspan 4} "состав"))
      (tr
        (td (:channels m))
        (td {:colspan 4} (:components m)))
      (tr
        (th "объем")
        (td {:colspan 4} (:scope m)))
      (tr
        (gen-th (list "ПО" "версия" "версия СИ" "контр. сумма"
                      "алгоритм")))
      (tr
        (td (:sw_name m))
        (td (:sw_version m))
        (td (:sw_version_real m))
        (td (:sw_checksum m))
        (td (:sw_algorithm m))))))

(defn methodology
  [m]
  (div
    (p "Методика поверки")
    (table
      (tr
        (th "id")
        (td (:methodology_id m))
        (th "наименование")
        (td (:methodology_short_name m)))
      (tr
        (th {:colspan 2} "полное наименование")
        (td {:colspan 2} (:methodology_name m))))))

(defn refs
  [m]
  (details
    (summary "Средства поверки")
    (table
      (tr
        (gen-th (list "" "тип" "id" "тип СИ" "компонент"
                      "значение" "зав. №" "номер 1С" "разряд"
                      "дата" "срок годности" "доступно")))
      (string/join
        "\n"
        (map (fn [m]
                 (tr
                  (td (:expiration m))
                  (td (:type m))
                  (td (:ref_id m))
                  (td (:mi_type m))
                  (td (:components m))
                  (td (:value m))
                  (td (:serial_number m))
                  (td (:number_1c m))
                  (td (:level m))
                  (td (:date m))
                  (td (:expiration_date m))
                  (td (:available m))))
             m)))))

(defn operations
  [m]
  (details
    (summary "Операции поверки")
    (table
      (tr
        (gen-th (list "id" "пункт НД" "наименование" "результат"
                      "причина непригодности" "комментарий")))
      (string/join
        "\n"
        (map (fn [m]
                 (tr
                   (td (:id m))
                   (td (:section m))
                   (td (:name m))
                   ;; Были проблемы с case при условии -1
                   (td (case (:result m)
                             0 "-"
                             1 "V"
                             -1 "X"))
                   (td (:unusability m))
                   (td (:comment m))))
             (:operations m))))))

(defn measurements
  ""
  [m]
  (details
    (summary "Результаты измерений")
    (table
      (tr 
        (gen-th (list "id" "metr_id" "ch_id" "канал"
                      "опорное" "измеренное" "значение 2"
                      "погрешность" "предел погрешности" "вариация")))
      (string/join
        "\n"
        (map (fn [m]
                 (tr
                   (td (:measurement_id m))
                   (td (:metrology_id m))
                   (td (:channel_id m))
                   (td (:channel_name m))
                   (if (< (:error_type m) 3)
                       (try
                         (let [res (pr/metrology-calc m)]
                           (str (td
                                  (string/replace
                                    (metr/round
                                      (:ref_value m)
                                      (let [discrete-val
                                          (if (:low_unit m)
                                              (:low_unit m)
                                              0.1)]
                                      (if (pos? (metr/exponent discrete-val))
                                          0
                                          (* -1 (dec (metr/exponent discrete-val))))))
                                    "." ","))
                                (td (:value res))
                                (td (:value_2 m))
                                (td (:error res))
                                (td (:error_string m))
                                (td
                                  (if (:variation res)
                                    (:variation res)
                                    "-"))))
                         (catch Exception e
                           (println
                             (str "metrology-calc Error!!!\n"
                                  (ex-message e)))))
                       (when (> (:error_type m) 5)
                         (td {:class "channel-cell" :colspan 6}
                             (:chr_string m))))))
             (:measurements m))))))

(defn report-row
  [m]
  (string/join
    "\n"
    (list
      (tr
        (td {:rowspan 11} (:id m))
        (td (:count m))
        (td (:protocol_number m))
        (td (if (= 1 (:verification_type m))
              "периодическая"
              "первичная"))
        (td {:colspan 3}
            (string/join
              " " 
              (list (:last_name m)
                    (:first_name m)
                    (:second_name m))))
        (td (:copy_from m)))
      (tr
        (td (:counteragent_id m))
        (td {:colspan 6} (:counteragent_name m)))
      (tr
        (td (:registry_number m))
        (td (:manufacture_year m))
        (td (:serial_number m))
        (td {:colspan 2}
            (:mi_type m))
        (td (:channels m))
        (td 
          (str
            (:area m) "; МПИ " (:interval m)
            " мес")))
      (tr
        (td {:colspan 7} 
          (if (> (count (:components m)) 1)
            (str
              "состав: "
              (:components m))
            (str
              "объем: "
              (:scope m)))))
      (tr
        (td {:colspan 2}
            (if (> 0 (count (:sw_name m)))
                (:sw_name m)
                "-"))
        (td (:sw_version m))
        (td (:sw_version_real m))
        (td {:colspan 2}
            (:sw_checksum m))
        (td (:sw_algorithm m)))
      (tr
        (td (:methodology_id m))
        (td {:colspan 6}
            (:methodology_name m)))
      (tr
        (td (:date m))
        (td (:temperature m))
        (td (:humidity m))
        (td (:pressure m))
        (td (:voltage m))
        (td (:frequency m))
        (td (:other m)))
      (tr
        (td (:condition_id m))
        (td (:real_temperature m))
        (td (:real_humidity m))
        (td (:real_pressure m))
        (td (:real_voltage m))
        (td (:real_frequency m))
        (td (:real_other m)))
      (tr
        (td {:colspan 7}
            (refs (:refs m))))
      (tr
        (td {:colspan 7}
            (operations m)))
      (tr
        (td {:colspan 7}
            (measurements m))))))

(defn records
  [coll]
  (table
    (string/join
      "\n"
      (map (fn [m] 
               (report-row m))
           coll))))

(defn verification-report
  "Создает содержимое файла report.html."
  [coll]
  (report "report" styles scripts
    (body
      (header
        #_(h1 "Условия выборки:"
           where))
      (main
        (records
          coll)))))

(defn conditions-report
  ""
  [coll]
  (report "conditions" gso-styles scripts
    (body
      (header)
      (main
        (table
          (thead
            (tr
              (th {:rowspan 2}
                  "id")
              (th "температура")
              (th "влажность")
              (th "давление")
              (th "напряжение")
              (th "частота"))
            (tr
              (th {:colspan 5}
                  "прочие"))
            (tr
              (th {:rowspan 2}
                  "дата")
              (th {:colspan 5}
                  "место"))
            (tr
              (th {:colspan 5}
                  "комментарий")))
          (tbody
            (string/join
              "\n"
              (map (fn [m]
                       (string/join
                        "\n"
                        (list
                          (tr
                            (td {:rowspan 2}
                                (:id m))
                            (td (:temperature m))
                            (td (:humidity m))
                            (td (:pressure m))
                            (td (:voltage m))
                            (td (:frequency m)))
                          (tr 
                            (td {:colspan 5}
                                (:other m)))
                          (tr
                            (td {:rowspan 2}
                                (:date m))
                            (td {:colspan 5}
                                (:location m)))
                          (tr
                            (td {:colspan 5}
                                (:comment m))))))
                   coll))))))))

(defn refs-report
  ""
  [coll]
  (report "refs" gso-styles scripts
    (body
      (header)
      (main
        (table
          (thead
            (tr
              (gen-th (list "id" "№ 1С" "тип"
                            "зав. №" "действительно до"))))
          (tbody
            (string/join
              "\n"
              (map (fn [m]
                       (tr
                         (td (:id m))
                         (td (:number_1c m))
                         (td (:mi_type m))
                         (td (:serial_number m))
                         (td (:expiration_date m))))
                   coll))))))))

(defn gso-report
  ""
  [coll]
  (report "gso" gso-styles scripts
    (body
      (header)
      (main
        (table
          (thead
            (tr
              (gen-th (list "id" "" "№ 1С" "тип" "наличие"
                            "состав" "значение" "погрешность"
                            "ед. изм." "№ паспорта" "дата"
                            "срок годности"))))
          (tbody
            (string/join
              "\n"
              (map (fn [m]
                       (tr
                         (td (:id m))
                         (td (:expiration m))
                         (td (:number_1c m))
                         (td (str (:type m) " " (:number m)))
                         (td (:available m))
                         (td (:components m))
                         (td (:concentration m))
                         (td (:uncertainity m))
                         (td (:units m))
                         (td (:pass_number m))
                         (td (:date m))
                         (td (:expiration_date m))))
                   coll))))))))

(defn counteragents-report
  ""
  [coll]
  (report "counteragents" gso-styles scripts
    (body
      (header
      (main
        (table
          (thead
            (tr
              (th {:rowspan 3} "id")
              (th {:colspan 3} "name"))
            (tr
              (th "inn")
              (th "type")
              (th "short_name"))
            (tr
              (th {:colspan 3} "address")))
          (tbody
            (string/join
              "\n"
              (map (fn [m]
                       (string/join
                         "\n"
                         (list
                           (tr
                             (td {:rowspan 3} (:id m))
                             (td {:colspan 3} (:name m)))
                           (tr
                             (td (:inn m))
                             (td (:type m))
                             (td (:short_name m)))
                           (tr
                             (td {:colspan 3} (:address m))))))
                   coll)))))))))

(defn full-methodology-table
  [m]
  (table
    (tr
      (gen-th (list "id" "рег. №" "дата от" "дата до" "сокращенная")))
    (tr
      (td (:id m))
      (td (:registry_number m))
      (td (:date_from m))
      (td (:date_to m))
      (td (:limited m)))
    (tr
      (th "название")
      (td {:colspan 4} (:name m)))
    (tr
      (th "сокращенное")
      (td {:colspan 4} (:short_name m)))
    (tr
      (th "СИ")
      (td {:colspan 4} (:mi_name m)))
    (tr
      (th "тип СИ")
      (td {:colspan 4} (:mi_types m)))))

(defn conditions-table
  [m]
  (table
    (tr
      (gen-th (list "темп." "влажн." "давл." "напряж." "частота")))
    (tr
      (td (:temperature m))
      (td (:humidity m))
      (td (:pressure m))
      (td (:voltage m))
      (td (:frequency m)))
    (tr
      (th "прочие")
      (td {:colspan 4} (:other m)))))

(defn operations-table
  [coll]
  (table
    (tr
      (gen-th (list "id" "пункт" "вид поверки" "наименование" "инфо")))
    (string/join "\n"
      (map (fn [m]
               (tr
                 (td (:id m))
                 (td (:section m))
                 (td (:verification_type m))
                 (td (:name m))
                 (td (:info m))))
           coll))))

(defn metrology-row
  [m]
  (str
    (tr
      (td {:rowspan 2} (:metrology_id m))
      (td (:channel_id m))
      (td (:channel m))
      (td (:type_id m))
      (td (:name m))
      (td (:chr_string m))
      (td (:operation_id m)))
    "\n"
    (tr
      (string/join "\n"
        (map (fn [s]
                 (td {:colspan 2} (s m)))
             (list :channel_name :channel_comment :metrology_comment))
        #_(td {:colspan 2} (:channel_name m))
        #_(td {:colspan 2} (:channel_comment m))
        #_(td {:colspan 2} (:metrology_comment m))))))

(defn metrology-table
  [coll]
  (table
    (tr
      (th {:rowspan 2} "МХ id")
      (gen-th (list "канал id" "канал" "тип id"
                     "имя МХ" "МХ" "операция")))
    (tr
      (string/join "\n"
        (map (fn [s]
                 (th {:colspan 2} s))
             (list "имя канала" "коммент канала" "коммент МХ"))))
    (string/join "\n"
      (map (fn [m]
               (metrology-row m))
           coll))))

(defn methodology-report
  [coll]
  (report "methodology" styles scripts
    (body
      (header)
      (main
        (string/join "\n"
          (map (fn [m]
                   (div
                     (hr)
                     (p "Сведения о методике поверки")
                     (full-methodology-table m)
                     (p "Условия поверки")
                     (conditions-table m)
                     (p "Операции поверки")
                     (operations-table (:operations m))
                     (p "Метрологические характеристики")
                     (metrology-table (:metrology m))))
               coll))))))

(comment

(require '[clojure.string :as string])

(require '[mapp.lib.gen-html :refer :all])

(require '[mapp.lib.metrology :as metr])

(operations {:operations {:id 280 :section 3 :name "name" :result 0
             :unusability "text" :comment nil}})

(:operations {:operations {:id 280 :section 3 :name "name" :result 0
             :unusability "text" :comment nil}})

)
