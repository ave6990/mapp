(ns cljs.table)

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


