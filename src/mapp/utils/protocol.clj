(ns mapp.utils.protocol
  (:require [clojure.math :as math]
            [clojure.string :as string]
            [clojure.pprint :refer [pprint]]
            [hiccup2.core :as h]
            [net.cgrand.enlive-html :as enlive]
            [mapp.lib.metrology :as metr]))

(defn enlive->hiccup
   [el]
   (if-not (string? el)
     (->> (map enlive->hiccup (:content el))
       (concat [(:tag el) (:attrs el)])
       (keep identity)
       vec)
     el))

(defn html->enlive 
  [html]
  (first (enlive/html-resource (java.io.StringReader. html))))

(defn html->hiccup [html]
  (-> html
      html->enlive
      enlive->hiccup))

(defn field
  "Возвращает html поле для вывода данных в протокол."
  [name value]
  [:div {:class "field"}
    [:p
      [:strong (str name ": ")]
      (str value ".")]])

(defn date-iso->local
  "Преобразует дату из формата ISO в локальный формат."
  [s]
  (->> (string/split s #"\-")
       reverse
       (string/join ".")))

(defn find-nbsp-place
  "Соблюдение требований к размещению неразрывных пробелов."
  [s]
  (->>
    (map (fn [re]
             (map (fn [el]
                      (first el))
                  (re-seq re s)))
             (list #"\d+\s+(\-|÷|±)\s+\d+"
                   #"\d+\)?\s+(см|кПа|Па|млн|с|м|кг|г|%|°C|\()"
                   #"(\.|орт)\s+№"
                   #"(№|СО)\s+\d"
                   #"[а-я]+\s+(НД)"
                   #"(р(\-|ай)он\.?|ул\.?|г\.?|д(ом)?\.?)\s+[№а-яА-Я]+"
                   #"[а-яА-Я]+\s+(р(\-|ай)|обл\.?|ул\.?|г\.?|д\.?)"))
    (apply concat)
    set))

(defn bsp->nbsp
  ""
  [s]
  (reduce (fn [a b]
              (string/replace a
                              b
                              (string/replace b " " " ")))
          s
          (find-nbsp-place s)))

(defn protocol-number
  [m]
  (str (:department m)
       "/" (:engineer m)
       "-" (:protocol_number m)
       "-" (:year m)))

(defn protocol-header
  ""
  [m]
  [:header
    {:class "header1"}
    [:img {:id "rst_ocsm"
           :src "/img/rst_ocsm.png"}]
    [:div
      {:id "address"}
      [:p
        [:strong
          "Федеральное бюджетное учреждение «Государственный региональный центр стандартизации, метрологии и испытаний в Оренбургской области»"]]
      [:p [:strong "(ФБУ «ОРЕНБУРГСКИЙ ЦСМ»)"]]
      [:p "460021, РОССИЯ, Оренбургская область, город Оренбург, улица 60 лет Октября, 2Б;"]
      [:p "тел/факс (3532) 33-37-05, факс (3532) 33-00-76, e-mail: info@orencsm.ru, http://www.orencsm.ru/"]]
    [:hr]
    [:p "Уникальный номер записи об аккредитации в реестре аккредитованных лиц  № RA.RU.311228"]
    [:hr]
    [:p "460021, РОССИЯ, Оренбургская область, город Оренбург, улица 60 лет Октября, 2Б"]
    [:p {:class "comment"} "место осуществления деятельности"]
    [:br]
    [:p {:class "capitalize"} "протокол "
                             (:verification_type m)
                            " поверки"]
    [:p (str "№ " (protocol-number m))
       " от "
       [:time (date-iso->local (:date m))]
       " г."]])

(defn page-footer
  ""
  [m n-page count-page]
    [:footer 
      [:p 
         "Протокол "
         (:verification_type m)
         " поверки № " 
         (protocol-number m)
         " от "
         [:time (date-iso->local (:date m))]]
      [:p
         "Страница "
         n-page
         " из "
         [:span {:contenteditable "true"}
            count-page]]])

(defn page-1-conditions
  [m]
  (field "Условия поверки"
   (str "температура воздуха: "
        (:temperature m)
        " " (:pr_temperature m) "; "
        "относительная влажность: "
        (:humidity m)
        " " (:pr_humidity m) "; "
        "атмосферное давление: "
        (:pressure m)
        " " (:pr_pressure m)
        (if-let [other (:other m)]
                other
                ""))))

(defn page-1-refs-set
  [m]
  (field "Средства поверки"
     (str (:mi_references m)
          (if-let [opt-ref (:optional_references m)]
                  opt-ref
                  ""))))

(defn page-1-sign
  [m]
  [:p {:class "sign"}
        "Подпись лица выполнявшего поверку "
        [:span {:class "placeholder"} "____________________ "]
        [:img {:class "sign_img"
              :src (str "/img/signs/sign_"
                        (math/round (mod (* 100 (rand)) 72))
                        ".png")}]
        (:engineer_name m)])

(defn page-1
  [m]
  "Первая страница протокола."
  [:section {:class "page_1"}
    (protocol-header m)
    [:main
      (field "Наименование, тип СИ"
             (str (:name m)))
      [:div {:class "two-column"}
        [:p
          [:strong "Заводской (серийный) номер СИ: "]
          (str (:serial_number m) ".")]
        [:p
          [:strong "Год изготовления СИ: "]
          (str (:manufacture_year m)
               " г.")]]
      (field "Регистрационный номер типа СИ (№ ГРСИ)"
             (:registry_number m))
      (field "В составе"
             (:components m))
      (field "Объем поверки"
             (:scope m))
      (field "Наименование, адрес заказчика"
             (str (:counteragent m) "; "
                  (:address m)))
      (field "НД на поверку"
             (:methodology m))
      (page-1-conditions m)
      (page-1-refs-set m)
      [:div {:class "field"}
        [:p #_{:class "capitalize"}
          [:strong "Результаты поверки приведены в приложении, без
            приложения протокол недействителен."]]]
      #_[:div {:class "field"}
        (html->hiccup (str "<ol>" (:operations m) "</ol>"))]
      (field "Заключение"
             (:conclusion m))
      (page-1-sign m)
      [:p "Сведения о результатах поверки переданы в ФИФ ОЕИ."]
      [:br]
      [:p {:class "comment"}
         "Данный протокол может быть воспроизведен только полностью.
         Любое частичное воспроизведение содержания протокола возможно
         только с письменного разрешения ФБУ «Оренбургский ЦСМ»"]]
    (page-footer m 1 2)])

(defn sw-version
  ""
  [m]
  [:table {:class "measurement-table"}
    [:thead
      [:tr
        [:th "Идентификационное наименование ПО"]
        [:th "Идентификационный номер ПО"]
        (if (:sw_version_real m)
            [:th "Действительный идентификационный номер ПО"])
        [:th "Цифровой идентификатор ПО"]
        [:th "Алгоритм вычисления цифрового идентификатора ПО"]]]
    [:tbody
      [:tr
         (map (fn [s]
                  (when s
                        [:td {:class "centered-cell"} s]))
              (list (:sw_name m)
                    (:sw_version m)
                    (if (:sw_version_real m)
                        (:sw_version_real m)
                        nil)
                    (if (:sw_checksum m)
                        (:sw_checksum m)
                        "-")
                    (if (:sw_checksum m)
                        (:sw_algorithm m)
                        "-")))]]])

(defn get-discrete-val
  [m]
  (if (:low_unit m)
      (:low_unit m)
      0.1))

(defn get-val
  [m discrete-val]
  (if (:value m)
      (metr/discrete
        (:value m)
        discrete-val)))

(defn get-val2
  [m discrete-val]
  (if (:value_2 m)
      (metr/discrete
        (:value_2 m)
        discrete-val)))

(defn get-exp
  [discrete-val]
  (if (pos? (metr/exponent discrete-val))
      0
      (inc (* -1 (metr/exponent discrete-val)))))

(defn get-ref
  [m exp]
  (metr/round
    (:ref_value m)
    exp))

(defn get-err
  [m ref val]
  (if (:value m)
      (metr/error val
                  ref
                  (:r_from m)
                  (:r_to m))))

(defn get-vari
  [m ref val val2]
  (if (:value_2 m)
      (metr/round 
        (metr/variation
          val2
          val
          ref
          (:error m)
          (:error_type m)
          (:r_from m)
          (:r_to m))
        2)
      "-"))

(defn metrology-calc
  ""
  [m]
  (let [discrete-val (get-discrete-val m)
        val (get-val m discrete-val)
        val2 (get-val2 m discrete-val)
        exp (get-exp discrete-val)
        ref (get-ref m exp)
        err (get-err m ref val)
        vari (get-vari m ref val val2)]
    (if (:value m)
        (hash-map
          :value (string/replace val "." ",")
          :value2 (if (nil? val2) nil (string/replace val2 "." ","))
          :ref (string/replace ref "." ",")
          :error
            (string/replace
              (case (:error_type m)
                    0 (metr/round
                        (:abs err)
                        exp)
                    1 (metr/round (:rel err) 1)
                    2 (metr/round (:red err) 1))
              "." ",")
          :variation
            (string/replace
              vari
              "." ","))
        (hash-map :value "-" :ref "-" :error "-" :variation "-"))))

(defn measurements-table-header
  []
  [:thead
    [:tr 
      [:th "Канал измерений, диапазон"]
      [:th "Опорное значение"]
      [:th "Измеренное значение"]
      [:th "Действительное значение основной погрешности"]
      [:th "Предел допускаемого значения основной погрешности"]
      #_[:th "Вариация показаний"]]])

(defn measurements-table-create-row
  [m]
  (try
     [:tr
       [:td {:class "channel-cell"}
           (str (:channel_name m))]
       (let [res (metrology-calc m)]
         (list
           [:td {:class "centered-cell"}
                ;{TOFIX} use round
                (string/replace
                  (:ref res)
                  "." ",")]
           [:td {:class "centered-cell"}
                (:value res)]
           [:td {:class "centered-cell"}
                (:error res)]
           [:td {:class "centered-cell"}
                (:error_string m)]
           #_[:td {:class "centered-cell"}
                (if (:variation res)
                  (:variation res)
                  "-")]))]
     (catch Exception e
       (print (ex-message e))
       (pprint m))))

(defn measurements-table
  ""
  [coll]
  (if (not (zero? (count coll)))
    [:li {:class "appendix-section"}
      [:p (:name (first coll))]
      [:table {:class "measurement-table"}
        (measurements-table-header) 
        [:tbody
          (map measurements-table-create-row
               coll)]]]))

(defn time-table
  [m]
  [:li {:class "appendix-section"}
    [:p (:name (first m))]
    [:table {:id "time-table"}
      [:thead
        [:tr
          [:th "Канал измерений, диапазон, с"]
          [:th "Действительное значение, с"]
          [:th "Значение по НД"]]]
      [:tbody
        (for [r m]
             [:tr
               [:td {:class "channel-cell"}
                    (str (:channel_name r))]
               [:td {:class "centered-cell"} (:value r)]
               [:td {:class "centered-cell"} (:error r)]])]]])

(defn variations-table-header
  []
  [:thead
    [:tr 
      [:th "Канал измерений, диапазон"]
      [:th "Опорное значение"]
      [:th "Измеренное значение со стороны меньших значений"]
      [:th "Измеренное значение со стороны больших значений"]
      [:th "Вариация показаний"]]])

(defn variations-table-create-row
  [m]
  (try
     [:tr
       [:td {:class "channel-cell"}
           (str (:channel_name m))]
       (let [res (metrology-calc m)]
         (pprint res)
         (list
           [:td {:class "centered-cell"}
                ;{TOFIX} use round
                (string/replace
                  (:ref res)
                  "." ",")]
           [:td {:class "centered-cell"}
                (:value res)]
           [:td {:class "centered-cell"}
                (:value2 res)]
           [:td {:class "centered-cell"}
                (if (:variation res)
                  (:variation res)
                  "-")]))]
     (catch Exception e
       (print (ex-message e))
       (pprint m))))

(defn variation
  [meas]
  [:li {:class "appendix-section"}
      [:p (:name (first meas))]
      [:table {:class "variations-table"}
        (variations-table-header) 
        [:tbody
          (map variations-table-create-row
               meas)]]])

(defn custom-html
  ""
  [coll]
  (if (not (zero? (count coll)))
      (string/join
        "\n"
        (map (fn [m]
                 (:html m))
             coll))))

(defn operation-conclusion
  [m]
  (let [value (:value m)
        unusability (:unusability m)]
    [:em
      (if (zero? value)
        "-"
        (str (if (= 1.0 value) "" "не ")
             "соответствует требованиям п. "
             (:section m) " МП"
             (if (nil? unusability)
                 "." 
                 (str " (" unusability ")."))))]))

(defn common-operation
  [meas]
  (map (fn [m]
           [:li {:class "appendix-section"}
                [:p (str (:name m) ": ")
                    (operation-conclusion m)]])
      meas))

(defn operations
  [m]
  (let [meas (:measurements m)
        ops (apply sorted-set
                   (map #(:operation_id %)
                        meas))
        sections (doall (map (fn [id]
                          (filter #(= id (:operation_id %))
                                  meas))
                        ops))]
    (doall (map (fn [sctn]
              (let [err-type (-> sctn first :error_type)
                    val2 (-> sctn first :value_2)
                    ch-name (-> sctn first :channel_name)]
                (println err-type)
                (println ch-name)
                (cond (nil? err-type)
                        (if (= "software" ch-name)
                            [:li {:class "appendix-section"}
                                [:p "Подтверждение соответствия программного обеспечения:"
                                    (operation-conclusion (first sctn))]
                                (sw-version m)]
                            (common-operation sctn))
                      (and (<= 0 err-type) (> 3 err-type))
                        (if (nil? val2)
                            (measurements-table sctn)
                            (variation sctn))
                      (and (< 5 err-type) (> 8 err-type))
                        (time-table sctn))))
          sections))))

(defn page-2
  "Приложение к протоколу поверки."
  [m]
  [:section {:class "page_2"}
    [:header {:class "header2"}
      [:p
        "Приложение к протоколу " 
             (:verification_type m)
             " поверки "
        "№ " (:department m)
        "/" (:engineer m)
        "-" (:protocol_number m)
        "-" (:year m) " от "
        [:time (date-iso->local (:date m))]
        " г."]]
    [:main
      [:div {:class "field"}
        [:p {:class "capitalize"}
          [:strong "результаты поверки"]]
      (if (zero? (count (:html m)))
          [:ol
            (operations m)
            #_(when (:sw_version m)
                  (sw-version m))
            #_(measurements-table (:measurements m))]
          [:ol
            (custom-html (:html m))])]
    (page-footer m 2 2)]])

(defn protocol
  ""
  [m]
    [:article
      (page-1 m)
      (page-2 m)])

(defn protocols
  ""
  [verifications]
  (bsp->nbsp
    (str
      (h/html
        [:html
          [:head
            [:meta {:charset "utf-8"}]
            [:meta {:name "author" :content "Aleksandr Ermolaev"}]
            [:meta {:name "e-mail" :content "ave6990@ya.ru"}]
            [:meta {:name "version" :content "2023-04-19"}]
            [:title "protocols"]
            [:link {:rel "stylesheet"
                    :type "text/css"
                    :href "/css/protocols.css"}]
            [:script {:src "/js/protocols.js"
                      :type "application/javascript"}]]
          [:body
             (map (fn [m] (protocol m))
                  verifications)]]))))
