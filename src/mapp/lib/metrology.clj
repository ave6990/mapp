(ns mapp.lib.metrology
  (:require
    [clojure.math :as math]))

(defn error
  "Порешность измерения."
  ([x y]
   {:abs (- x y)
    :rel (if-not (zero? y)
                 (* (double (/ (- x y) y)) 100)
                 nil)
    :red nil})
  ([x y a]
   (let [res (error x y)]
     {:abs (:abs res)
      :rel (:rel res)
      :red (* (double (/ (- x y) a)) 100)}))
  ([x y a b]
   (error x y (- b a))))

(defn variation
  "Возвращает значение вариации в долях от основной погрешности."
  ([vg vl v-ref err err-type r-from r-to]
   (case err-type
     0 (double (/ (- vg vl) err))
     1 (* 100 (double (/ (- vg vl) v-ref err)))
     2 (* 100 (double (/ (- vg vl) (- r-to r-from) err)))))
  ([vg vl err]
   (variation vg vl 0 err 1 0 0))
  ([vg vl v-ref err r-from r-to]
   (variation vg vl 0 err 2 r-from r-to)))

(defn delta
  "Возвращает границы диапазона значений 
   (value - tolerance, value + tolerance)
   , если передан третий аргумент, то значение tolerance расчитывается как
   процент от value."
  ([value tolerance percent]
   (let [dif (double (/ (* value tolerance) 100))]
     (list (- value dif) (+ value dif))))
  ([value tolerance]
   (list (- value tolerance) (+ value tolerance))))

(defn range-converter
  "Возвращает функцию-конвертер диапазонов."
  [s-min s-max d-min d-max]
  (fn
    [val]
    (+ d-min (/ (* (- val s-min) (- d-max d-min)) (- s-max s-min)))))

(defn round
  "Возвращает значение округленное до заданного знака после запятой или
   до целых, если передан один аргумент."
  ([x y]
   (/ (math/round (* x (math/pow 10 y)))
      (math/pow 10 y)))
  ([x]
   (round x 0)))

(defn average
  "Возращает среднее арифметическое переданных числовых значений."
  [& xs]
  (double (/
           (reduce + xs)
           (count xs))))

(defn discrete
  "Округляет значение с учетом заданной дискретности."
  [val discrete-val]
  (let [exp
        (let [t-val (math/log10 discrete-val)]
          (if (pos? t-val)
              (* -1 (inc (math/ceil t-val)))
              (* -1 (inc (math/floor t-val)))))]
    (round
     (+ (round val exp)
        (* discrete-val
           (round (double (/ 
                           (- val (round val exp))
                           discrete-val)))))
     (inc exp))))

(defn exponent
  "The function return an exponent of the SCI expression of a number."
  [val]
  (let [t-val (if (zero? val)
                  0
                  (math/log10 val))]
    (if (pos? t-val)
        (math/ceil t-val))
        (math/floor t-val)))

(defn sko
  "Функция вычисления среднего квадратического отклонения."
  [& xs]
  (math/sqrt
   (/
     (reduce +
             (map
              (fn [x] (math/pow (- x (apply average xs)) 2))
              xs))
     (dec (count xs)))))

(defn tolerance
  "Возвращает значение допускаемой основной погрешности выраженное
   в абсолютных единицах.
   :m (hash-map :value ; error nominal
                :error_type ; error type
                :ref_value ; references value
                :r_from ; start point of range
                :r_to ; end point of range"
   [m]
   (cond (zero? (:error_type m))
           (if (:fraction m)
               (+ (* (:error m) (:fraction m))
                  (if (:value m)
                      (:value m)
                      0))
               (:error m))
         (= (:error_type m) 1)
           (double (/ (* (:error m) (:ref_value m)) 100))
         (= (:error_type m) 2)
           (double (/ (* (:error m) (- (:r_to m) (:r_from m))) 100))
         (or (= (:error_type m) 6)
             (= (:error_type m) 7))
           (* (:error m) 0.15)
         (= (:error_type m) 16)
           1))  ;NB random value ;NB use `case` instead `cond`

(defn ^:private get-channels
  [m]
  (set (map (fn [meas]
                (:channel_id meas))
            m)))

(defn ^:private get-channels-k
  [coll]
  (let [k1 0.6]
    (reduce (fn [m v]
                (assoc m
                       v
                       (- (* (rand) 2 k1) k1)))
            {}
            (get-channels coll))))

#_(defn gen-value
  "Возвращает случайное число в пределах основной погрешности."
  [m]
  (let [k1 (if (:channel_error m)
               (:channel_error m)
               0.6) 
        k2 0.15
        ref (if (or (= (:error_type m) 6)
                    (= (:error_type m) 7))
                (* (:error m) 0.8)
                (:ref_value m))
        channel-error (if (zero? ref)
                          0
                          (- (rand (* 2 k1)) k1))
        meas-error (- (rand (* 2 k2)) k2)
        low-unit (if (= (:error_type m) 6)
                     1
                     (if (:low_unit m)
                         (:low_unit m)
                         0.1))
        res (discrete (+ ref
                         (* (tolerance m)
                            (+ channel-error meas-error)))
                      low-unit)]
    (if (< (:error_type m) 3)
        (cond (and (:view_range_from m)
                   (< res (:view_range_from m)))
                (:view_range_from m)
              (and (:view_range_to m)
                   (> res (:view_range_to m)))
                (:view_range_to m)
              :else
                res)
        res)))

(defn gen-value
  "Возвращает случайное число в пределах основной погрешности."
  [m]
  (let [k1 (if (:channel_error m)
               (:channel_error m)
               0.6) 
        k2 0.15
        ref (if (or (= (:error_type m) 6)
                    (= (:error_type m) 7))
                (* (:error m) 0.8)
                (if (:ref_value m)
                    (:ref_value m)
                    0))
        channel-error (if (zero? ref)
                          0
                          (- (rand (* 2 k1)) k1))
        meas-error (- (rand (* 2 k2)) k2)
        low-unit (cond
                  (= (:error_type m) 6)
                    1
                  (= (:error_type m) 16)
                    (if (:fraction m)
                        (:fraction m)
                        1)
                  (:low_unit m)
                    (:low_unit m)
                  :else 0.1)
        res (discrete (+ ref
                         (* (tolerance m)
                            (+ channel-error meas-error)))
                      low-unit)]
    (cond
      (< (:error_type m) 3)
        (cond (and (:view_range_from m)
                   (< res (:view_range_from m)))
                (:view_range_from m)
              (and (:view_range_to m)
                   (> res (:view_range_to m)))
                (:view_range_to m)
              :else
                res)
      (= (:error_type m) 16)
        (round (+ (rand (- (:r_to m) (:r_from m)))
                  (:r_form m))
               low-unit)
      :else res)))

(defn gen-values
  [coll]
  (let [k1 0.6
        channels-k (get-channels-k coll)]
    (map (fn [m]
             (println m)
             {:measurement_id (:measurement_id m)
              :value
                (try
                  (if (nil? (:metrology_id m))
                      1
                      (->>
                        (:channel_id m)
                        (get channels-k)
                        (assoc m :channel_error)
                        gen-value))
                  (catch Exception e
                    (println (str
                                "gen-value error!!! \n"
                                (ex-message e)))))})
         coll)))

(defn air-v->vnc
  [v t p]
  (/ (* v p 293.2) (* (+ t 273.2) 101.3)))

(defn air-vnc->v
  [v t p]
  (/ (* v (+ t 273.2) 101.3) p 293.2))

(comment

(error 43 45 10 50)

)
