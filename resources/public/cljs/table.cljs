(ns cljs.table
  (:require
    [clojure.string :as string]
    [cljs.dom-functions :refer :all]))

(defn create-header
  [model]
  [:tr
    (for [[id nm _] model]
         [(keyword (str "th.col" (keyword id)))
           nm])])

(defn create-row
  [model row-data]
  (let [ids (for [[id _ _] model] id) 
        editables (for [[_ _ editable] model] (str editable))]
    [:tr 
      (map (fn [id editable]
               [(keyword (str "td.col" (keyword id)))
                    {:contenteditable editable}
                 ((keyword id) row-data)])
           ids
           editables)]))

(defn create-rows
  [model data]
  (map (fn [row]
           (create-row model row))
       data))

(defn create
  "args: model [[id name visibility] [] ... []]"
  [id model data]
  [:table {:id id}
    [:thead
      (create-header model)]
    [:tbody
      (create-rows model data)]])

(defn get-field
  [el]
  (-> el
      (.getAttribute "class")
      (string/replace #"col:" "")
      keyword))

(defn get-data
  [el]
  (let [s (-> el .-innerHTML)]
    (if (= "" s)
        nil
        s)))

(defn get-cell-data
  [el]
  {(get-field el)
   (get-data el)})

(defn read-row
  [row]
  (let [cols (-> row .-childNodes seq)]
    (reduce (fn [o el]
                (let [editable (-> el (.getAttribute "contenteditable"))]
                  (if (= "true" editable)
                      (conj o (get-cell-data el))
                      o)))
            {}
            cols)))

(defn read-selected-rows
  []
  (let [rows (get-by-class "selected")]
    (vec
      (for [row rows]
           (read-row row)))))

