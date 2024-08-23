(ns mapp.lib.gs2000
  (:require [mapp.lib.chemistry :as ch]))

(def passports
  "Коэффициенты разбавления генераторов (паспортные значения)."
  [{:serial_number "58-2-21"
    :air [1176 513 253 134 70.4 46.8 36.3 24.3 14 10.5]
    :N2 [1196 522 267 136 71.6 47.6 36.9 24.7 14.2 10.7]}
   {:serial_number "87-1-23"
    :air [1160 489 237 140 70.3 50.4 34.6 21.8 13.6 10.9]
    :N2 [1180 497 241 142 71.5 51.3 35.2 22.2 13.8 11.1]}
   {:serial_number "88-2-23"
    :air [1240 496 283 147 74.4 48.5 34.7 27.7 16.4 11]
    :N2 [1261 504 288 149 75.7 49.3 35.3 28.2 16.7 11.2]}])

(def components 
  "Перечень газов подлежащих разбавлению с помощью генератора,
   согласно РЭ."
  ["NO" "NO2" "N2O" "NH3" "H2" "H2S" "SO2" "O2" "CO"
    "CO2" "CS2" "CH4" "C2H6" "C3H8" "C4H10" "C5H12"
    "C6H14" "CH3OH" "CH3SH" "CH3OCH3" "C2H5OH" "C2H4O"])

(defn nearest-num
  "Возвращает число из коллекции наиболее близкое к заданному."
  [x coll]
  (first
   (sort (fn [y z]
             (cond
              (< (abs (- y x)) (abs (- z x))) -1
              (> (abs (- y x)) (abs (- z x))) 1
              :else 0))
         coll)))

(defn binary->digit-list
  "Возвращает последовательность 10 бит целого числа от 0 до 1023."
  [x]
  (->> (Integer/toString x 2)
       (map str)
       (map read-string)
       (concat (repeat (- 10 (count (Integer/toString x 2))) 0))))

(defn dilution-factor
  "Вычисляет коэффициент разбавления генератора, положение клапанов
   задается двоичным представлением целого числа."
  [x coll] 
  (if (zero? x)
    x
    (->> (map (fn [x, y]
                (if (zero? x)
                    x
                    (double (/ 1 (dec y)))))
              (binary->digit-list x)
              coll)
         (reduce +)
         (/ 1)
         double
         inc)))

(defn calc-factors
  ""
  [coll]
  (vec (map (fn [n]
                (dilution-factor n coll))
            (range 1 1024))))

(defn calculator ;; TODO: make an adjustment for h2s for small concentration value
  "Возвращает функцию расчета режима работы ГС-2000.
   В качестве аргумента передается паспорт генератора.
   Возвращаемая функция f принимает 3 или 4 аргумента:
   (f газ-разбавитель исходная-концентрация целевая-концентрация)
   исходная и целевая концентрации заданы в млн^-1
   (f исходный-газ газ-разбавитель исходная-концентрация целевая-концентрация)
   исходная концентрация задана в млн^-1, целевая концентрация и результат -
   мг/м^3."
  [m]
  (let [pass
          {:air (calc-factors (:air m))
          :N2 (calc-factors (:N2 m))}]
     (letfn [(f 
               ([sym x y]
                (let [factor (nearest-num (double (/ x y)) (sym pass))]
                 (hash-map :conc (/ x factor)
                           :valves (->
                                     (sym pass) 
                                     (.indexOf factor)
                                     inc
                                     binary->digit-list))))
               ([s sym x y]
                (let [res (f sym x (ch/mg->ppm s y))]
                 {:conc (ch/ppm->mg s (:conc res))
                  :valves (:valves res)})))]
       f)))

(defn re-calculate
  "Расчитать концентрацию газа по заданному состоянию клапанов."
  [m sym x y]
    (double (/ x (dilution-factor y (sym m)))))
 
(comment

((calculator (passports 1)) :air 21200 12000)

((calculator (passports 1)) "CH4" :air 21200 350)

(def f (/ 21200 5000))

(def p (calc-factors (:air (passports 1))))

(def fp (nearest-num f p))

(/ 21200 fp)

(ch/ppm->mg
  "CH4"
  (re-calculate (passports 1) :air 21200 2r1010010000))

(calc-factors (:air (passports 0)))

(dilution-factor 2r1000000101 '(2 2 3 4 5 6 7 8 9 10))

(binary->digit-list 2r0101)

(nearest-num 6.7 [1 2 3 4 5 6 7 8])

)
