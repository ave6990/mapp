(ns mapp.lib.chemistry
  (:require
   [clojure.core :refer :all]))

(def lel
  (hash-map
   "C3H8" 17000 "C3H6" 20000 "C4H10" 13000
   "C5H12" 15000 "C6H14" 10000 "C6H6" 12000
   "C2H4" 23000 "C2H2" 23000 "C2H6" 25000
   "H2" 40000 "H2S" 40000))

(def gases
  "Список химических формул некоторых газов."
  (list
   "N2" "NH3" "Ar" "C2H2" "C3H6O" "C4H10" "C4H9OH"
   "H2O" "H2" "C6H14" "He" "C7H16" "CO2" "C10H22"
   "C12H10" "C12H10O" "CH2Cl2" "C4H10O" "N2O" "HJ" "O2"
   "Kr" "Xe" "CH4" "CH5N" "CH4O" "Ne" "NOCl"
   "O3" "NO" "NO2" "CO" "C8H18" "C5H12" "C3H8"
   "C3H6" "H2Se" "SO2" "SO3" "H2S" "PH3" "CF3Cl"
   "CF2Cl2" "CFCl3" "F2" "SiF4" "CH3F" "Cl2" "HCl"
   "CH3Cl" "CHCl3" "C2N2" "HCN" "C2H6" "C2H7N" "C2H4"
   "C2H6O" "C2H5Cl" "CH3SH" "CS2" "CH3OCH3" "C2H4O" "C2H5SH"
   "C2H6S"))

(defn gas?
  "Возвращает истину, если переданная строка есть в списке gases."
  [s]
  (some (partial = s) gases))

(def molar-volume
  (hash-map "air" 24.06, "N2" 24.04))

(def atomic-weight 
  "IUPAC Periodic Table 150-04May22 atomic weight."
  (hash-map
   "H" 1.008
   "He" 4.0026
   "C" 12.011
   "N" 14.007
   "O" 15.999
   "F" 18.998
   "Ne" 20.18
   "Si" 28.085
   "P" 30.974
   "S" 32.06
   "Cl" 35.45
   "Ar" 39.95
   "Se" 78.971))

(defn molar-weight
  "Возвращает молярную массу вещества заданного формулой."
  [s]
  (reduce +
          (map (fn [v]
                   (let [sym (re-find #"[A-Z][a-z]?" v)
                         rest (clojure.string/replace v sym "")
                         val (if-not (= rest "") (read-string rest) 1)]
                     (* (atomic-weight sym) val)))
               ;;Разбивает формулу вещества на элементы
               (re-seq #"[A-Z][a-z]?\d{0,2}" s))))

(defn coefficient
  "Возвращает значение коэффициента пересчета из объемных
  единиц измерения концентрации в массовые."
  ([formula temp press]
    (when (gas? formula)
          (/ (* (molar-weight formula) press 273.15)
             (* 22.41 101.325 (+ temp 273.15)))))
  ([formula]
    (coefficient formula 20 101.325)))

(defn ppm->mg
  "Пересчет единиц концентрации млн⁻¹ в мг/м³."
  [s x]
    (* x (coefficient s)))

(defn mg->ppm
  "Пересчет единиц концентрации мг/м³ в млн⁻¹."
  [s x]
  (double (/ x (coefficient s))))

(comment

(molar-weight "CH4")

(gas? "C2N2")

(coefficient "H2S" 24 99.81)

)
