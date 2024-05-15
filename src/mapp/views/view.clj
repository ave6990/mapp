(ns mapp.views.view
  (:require
    [mapp.lib.gen-html :as h]))

(defn meta-tag
  [name content]
  (h/meta {:name name :content content}))

(defn gen-page-head
  [title]
  (h/head
    (h/meta {:charset "utf-8"})
    (meta-tag "author" "Aleksandr Ermolaev")
    (meta-tag "e-mail" "ave6990@ya.ru")
    (meta-tag "version" "2024-05-14")
    (h/title title)
    (h/link {:rel "stylesheet"
             :type "text/css"
             :href "/css/styles.css"})
    (h/script {:type "text/javascript"} "")))

(defn href
  [path name]
  (h/a {:href path} name))

(defn ^:private extract-data
  [ids data]
  (vec
    (map (fn [row]
             (vec
               (map (fn [id]
                        (id row))
                    ids)))
         data)))

(defn table 
  [model data]
  (let [ids (vec (map #(:id %) model))
        fields (vec (map #(:name %) model))]
  (h/table
    (h/table-header
      fields)
    (h/table-rows
      (extract-data ids data)))))

(def main-menu
  (h/nav {:id "main-menu"}
    (href "/" "Журнал ПР")
    (href "/gso" "ГСО")
    (href "/refs" "Эталоны")
    (href "/conditions" "Условия поверки")))

(def query-panel
  (h/div {:id "query-panel"}
    (h/input {:type "number"
              :id "page-number"
              :value "1"
              :min "1"})
    (h/input {:type "text"
              :id "query"})))

(defn gen-page
  [title content]
  (h/doctype
    (h/html
      (gen-page-head title)
      (h/body content))))
