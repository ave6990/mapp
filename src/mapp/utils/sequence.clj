(ns metrology.utils.sequence)

(defn in?
  "true if coll contains elm"
  [coll elm]
  (some (partial = elm) coll))

(defn unique
  "return unique values vector of collection"
  [coll]
  (reduce (fn [acc el]
              (if (in? acc el)
                  acc
                  (conj acc el)))
          []
          coll))

(defn slice
  "return 'cnt' elements of coll from position 'from'"
  [from cnt coll]
  (take cnt (drop from coll)))
