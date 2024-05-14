(ns mapp.lib.calc
  (:require
    [clojure.math :as math]))

(defn sheet-count
  "Вычисляет количество листов кровельного материала и лишнее
   расстояние для заданной ширины крыши и рабочей ширины листа."
  ([l width]
   (hash-map
     :count (math/ceil (double (/ l width)))
     :width (- l (* width (math/ceil (double (/ l width)))))))
  ([l]
   (sheet-count l 1.1)))


