(ns mapp.protocols.custom
  (:require
    [clojure.string :as string]
    [clojure.math :as math]
    [clojure.pprint :refer [pprint]]  ; нужно на этапе отладки
    [mapp.lib.metrology :as m]
    [mapp.lib.gen-html :refer :all]
    [mapp.utils.sequence :as sq]))

(defn point
  [name content]
  (li
    {:class "appendix-section"}
    name
    content))

(defn appendix
  [& l]
  (string/join
    "\n"
    l))

(defn str->float
  [s]
  (Float/parseFloat (string/replace s "," ".")))

(defn get-vals-by-key
  [k m]
  (map (fn [item]
           (k item))
       m))

;; #fp12#фп12#protocol
(defn fp12
  []
  (string/join
    "\n" 
    (list (li
            {:class "appendix-section"}
            "Внешний осмотр: "
            (em "соответствует требованиям п. 7.1 МП.")
            (p (em "Внешние повреждения и загрязнения отсутствуют. Маркировка соответствует требованиям НД.")))
          (li
            {:class "appendix-section"}
            "Опробование: "
            (em "соответствует требованиям п. 7.2 МП."))
          (li
            {:class "appendix-section"}
            "Проверка порога чувствительности: "
            (em "соответствует требованиям п. 7.3 МП.")
            (p (em "При подаче ПГС № 5 срабатывает прерывистая звуковая и световая сигнализация, заполнение шкалы 3/4.")))
          (li
            {:class "appendix-section"}
            "Проверка времени срабатывания сигнализации: "
            (em "соответствует требованиям п. 7.4 МП."))
          (li
            {:class "appendix-section"}
            "Проверка порога срабатывания и абсолютной погрешности: "
            (em "соответствует требованиям п. 7.5 МП.")
            (p (em "При подаче ПГС № 7 срабатывает постоянная звуковая и световая сигнализация отображается символ «П»."))))))

(defn pr-68261-17
  "ГХ-М (АМ-5), АМ-0059"
  [m]
  (let [meas (vec (:measurements m))
        t (str->float (:temperature m))
        p (str->float (:pressure m))
        vals (vec (map (fn [i]
                           (:value i))
                       meas))
        germ (get vals 0)
        ac-val (/ (apply + vals) 3)
        a-val (m/round ac-val 1)
        n-val (m/round
                (/ (* a-val p 293.2) (* (+ t 273.2) 101.3))
                1)
        err (m/round (- 100 n-val) 1)]
    (appendix
      (point
        "Герметичность:"
        (em
          (str
            (:value germ)
            " см³ (не более 3 см³).")))
      (point
        "Определение метрологических характеристик: "
        (table
          (tr
            (th {:colspan 3}
                "Измеренный объем, см³")
            (th "Среднее значение, см³")
            (th "Значение объема приведенное к Н. У., см³")
            (th "Абсолютная погрешность, см³")
            (th "Допускаемая асолютная погрешность, см³"))
          (string/replace
            (tr
              (td (get vals 1))
              (td (get vals 2))
              (td (get vals 3))
              (td a-val)
              (td n-val)
              (td err)
              (td 5)) "." ","))))))

(defn pr-35823-07
  "Altairt H2S"
  [m]
  (let [meas (vec (:measurements m))
        vals (vec (map (fn [i]
                           (:measurement_text i))
                       meas))
        refs (vec (map (fn [i]
                           (:ref_value i))
                       meas))
        desc (vec (map (fn [i]
                           (:metrology_text i))
                       meas))]
    (appendix
      (point
        "Определение основной абсолютной погрешности сигнализатора:"
        (table
          (tr
            (th "Пороги срабатывания сигнализации, млн⁻¹")
            (th "Номер ПГС")
            (th "Номинальное значение концентрации определяемого компонента ПГС, млн⁻¹")
            (th "Состояние сигнализации при подаче ПГС по НД")
            (th "Состояние сигнализации соответствует требованиям НД (да/нет)"))
          (string/replace
            (string/join "\n"
              (list
                (tr
                  (td {:rowspan 2}
                      7)
                  (td 1)
                  (td (get refs 0))
                  (td (get desc 0))
                  (td (get vals 0)))
                (tr
                  (td 2)
                  (td (get refs 1))
                  (td (get desc 1))
                  (td (get vals 1)))
                (tr
                  (td {:rowspan 2}
                      14)
                  (td 3)
                  (td (get refs 2))
                  (td (get desc 2))
                  (td (get vals 2)))
                (tr
                  (td 4)
                  (td (get refs 3))
                  (td (get desc 3))
                  (td (get vals 3)))
                ))
            "."
            ","))))))

(defn get-channels-list
  [meas]
  (sq/unique (get-vals-by-key
            :channel_name
            meas)))

(defn get-meas-by-channels
  [meas ch-count meas-count-by-channel]
  (map (fn [i]
           (sq/slice (* i meas-count-by-channel)
                  meas-count-by-channel
                  meas))
       (range ch-count)))

(defn pr-18482-noise
  [meas-by-channels]
  (->>
    meas-by-channels
    (map (fn [item]
             (let [v-item (vec item)
                   noise (get v-item 0)
                   ch-name (:channel_name noise)
                   drift (get v-item 1)]
               (tr
                 (str (td ch-name)
                      "\n"
                      (->>
                        (list noise
                              drift)
                        (map (fn [p]
                                 (string/join "\n"
                                   (list
                                     (td (:value p))
                                     (td (:addition p))
                                     (td (:metrology_units p))))))
                        doall
                        (string/join "\n")))))))
    doall
    (string/join "\n")))

(defn pr-18482-limit
  [meas-by-channels]
  (->>
    meas-by-channels
    (map (fn [item]
             (let [v-item (vec item)
                   limit (get v-item 2)
                   ch-name (:channel_name limit)]
               (tr
                 (->>
                  (list
                    (td (:value limit))
                    (td (:addition limit))
                    (td (:metrology_units limit)))
                  (string/join "\n")
                  (str (td ch-name) "\n"))))))
    doall
    (string/join "\n")))

(defn pr-18482-peaks
  [meas-by-channels]
  (->>
    meas-by-channels
    (map (fn [item]
             (let [v-item (vec item)
                   ch-name (:channel_name (get v-item 0))
                   t (vec (slice 3 5 v-item))
                   s (vec (slice 8 5 v-item))
                   h (vec (slice 13 5 v-item))
                   t48 (get v-item 19)
                   s48 (get v-item 20)
                   h48 (get v-item 21)]
              (string/join "\n"
                (list
                  (->>
                    (map (fn [ti si hi n]
                             (tr
                               (if (zero? n)
                                   (str (td {:rowspan 7} ch-name)
                                        "\n"
                                        (td (inc n)))
                                   (td (inc n)))
                               (td (:value ti))
                               (td (:metrology_units ti))
                               (td (:value si))
                               (td (:metrology_units si))
                               (td (:value hi))
                               (td (:metrology_units hi))))
                         t s h (range 5))
                    doall
                    (string/join "\n")
                    tr)
                  (tr
                    (->>
                      (list t s h)
                      (map (fn [vs]
                               (str
                                 (td
                                   (m/round
                                     (apply m/average
                                            (get-vals-by-key :value vs))
                                     3)
                                 "\n"
                                 (td (:metrology_units (get vs 0))))))
                      (string/join "\n")
                      (str (td "Среднее арифметическое") "\n")))
                  (tr
                    (td "Относительное СКО"))))))))
    doall
    (string/join "\n")))

(defn table-cells
  ([column-fn vs mask ]
    (->>
      (map (fn [row sp]
               (->>
                 (map (fn [v [rspan cspan]]
                          (column-fn {:rowspan rspan
                                      :colspan cspan}
                                     v))
                      row
                      sp)
                 (string/join "\n")
                 tr))
           vs
           mask)
      (string/join "\n")))
  ([column-fn vs]
    (table-cells
      column-fn
      vs
      (doall
        (map (fn [r]
                 (map (fn [_]
                          [1 1])
                      r))
             vs)))))

(defn table-header
  ([vs mask]
    (table-cells th vs mask))
  ([vs]
    (table-cells th vs)))

(defn table-rows
  ([vs mask]
    (table-cells td vs mask))
  ([vs]
    (table-cells td vs)))

(defn pr-18482-08
  "Кристалл-5000 NB_edit"
  [m]
  (let [meas (vec (:measurements m))
        ch-count (:channels m)
        meas-by-channels (get-meas-by-channels
                           meas
                           ch-count
                           21)]
    (appendix
      (point
        "Определение уровня флуктуационных шумов и дрейфа нулевого сигнала:"
        (table
          (table-header
            (list
              (list "Детектор" "Значение уровня шумов" "Значение дрейфа")
              (list "действительное" "допускаемое" "ед. изм."
                    "действительное" "допускаемое" "ед. изм."))
            (list
              (list [2 1] [1 3] [1 3])
              (list [1 1] [1 1] [1 1] [1 1] [1 1] [1 1])))
          (pr-18482-noise meas-by-channels)))
      (point
        "Определение предела детектирования:"
        (table
          (table-header
            (list
              (list "Детектор" "Действительное значение" "Допускаемое значение"
                    "Ед. изм.")))
          (pr-18482-limit meas-by-channels)))
      (point
        "Определение относительного СКО выходного сигнала:"
        (table
          (table-header
            (list
              (list "Детектор" "Время" "Площадь пика" "Высота пика")
              (list "Значение" "Ед. изм." "Значение" "Ед. изм."
                    "Значение" "Ед. изм."))
            (list
              (list [2 2] [1 2] [1 2] [1 2])
              (list [1 1] [1 1] [1 1] [1 1] [1 1] [1 1])))
          #_(pr-18482-peaks meas-by-channels))))))

(comment

  (require '[metrology.utils.sequence :refer :all] :reload)

  (clojure.math/round 0.2342)

  (rand-nth '(1 4 8))

  (get (vec '(1 2 3)) 2)

  (find-doc "slice")

  (doc peek)

)
