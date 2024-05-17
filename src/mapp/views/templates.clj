(ns mapp.views.templates
  (:require
    [hiccup2.core :as h]
    [mapp.views.verifications-settings :as vs]))

(defn meta-tag
  [name content]
  [:meta {:name name :content content}])

(defn gen-page-head
  [title]
  [:head
    [:meta {:charset "utf-8"}]
    (meta-tag "author" "Aleksandr Ermolaev")
    (meta-tag "e-mail" "ave6990@ya.ru")
    (meta-tag "version" "2024-05-14")
    [:title title]
    [:link {:rel "stylesheet"
             :type "text/css"
             :href "/css/styles.css"}]
    [:script {:crossorigin "false"
              :src "https://unpkg.com/react@17/umd/react.production.min.js"}]
    [:script {:crossorigin "false"
              :src "https://unpkg.com/react-dom@17/umd/react-dom.production.min.js"}]
    [:script {:src "https://cdn.jsdelivr.net/npm/scittle@0.6.17/dist/scittle.js"
              :type "application/javascript"}]
    [:script {:src "https://cdn.jsdelivr.net/npm/scittle@0.6.17/dist/scittle.reagent.js"
              :type "application/javascript"}]
    [:script {:src "https://cdn.jsdelivr.net/npm/scittle@0.6.17/dist/scittle.cljs-ajax.js"
               :type "application/javascript"}]
    [:script {:src "/cljs/main.cljs"
               :type "application/x-scittle"}]])

(defn href
  [path name]
  [:a {:href path} name])

(defn ^:private extract-data
  [ids data]
  (vec
    (map (fn [row]
             (vec
               (map (fn [id]
                        (id row))
                    ids)))
         data)))

(def main-menu
  [:nav {:id "main-menu"}
    (href "/" "Журнал ПР")
    (href "/gso" "ГСО")
    (href "/refs" "Эталоны")
    (href "/conditions" "Условия поверки")])

(defn query-panel
  [pages records]
  [:div {:id "query-panel"}
    "стр. "
    [:input {:type "number"
              :id "page-number"
              :value "1"
              :min "1"
              :max pages}]
    " из "
    [:span {:id "pages-count"} pages]
    " ("
    [:span {:id "records-count"} records]
    ") "
    [:input {:type "text"
              :id "query"}]])

(defn gen-page
  [title content]
  [:html
    (gen-page-head title)
    content])

(def footer
  [:footer
      [:p "Mapp, версия 2024-05-15"]])

(defn page-template 
  [toolbar query-panel table edit-panel]
  [:body
    [:header
      main-menu]
    [:main
      [:section {:id "toolbar-panel"} toolbar]
      [:section {:id "query-panel"} query-panel]
      [:section {:id "table-panel"} table]
      [:section {:id "edit-panel"} edit-panel]]
    footer])

(defn create-table-header
  [model]
  [:tr
    (for [[id nm _] model]
      [:th {:class (str "col" id)}
                 nm])])

(defn create-table-row
  [model row-data]
  (let [ids (for [[id _ _] model] id) 
        editables (for [[_ _ editable] model] (str editable))]
    [:tr 
      (map (fn [id editable]
               [:td {:class (str "col" id)
                    :contenteditable editable}
                 (id row-data)])
           ids
           editables)]))

(defn create-table-rows
  [model data]
  (map (fn [row]
           (create-table-row model row))
       data))

(defn create-table
  "args: model [[id name visibility] [] ... []]"
  [id model data]
  [:table {:id id}
    [:thead
      (create-table-header model)]
    [:tbody
      (create-table-rows model data)]])

(comment

(require '[hiccup2.core :as h] :reload)

(def t-page
  (gen-page "Test" page-template))

(str (h/html [:p]))

(str (h/html t-page))

(str (h/html (meta-tag "author" "ave6990")))

(create-table-header
          '([:id "id" true]
            [:name "name" true]))

(str (h/html
(create-table-row
  '([:id "id" true]
    [:name "name" true])
  {:id 123 :name "peter"})))

(str (h/html
(create-table-rows
  '([:id "id" true]
    [:name "name" true])
  '({:id 123 :name "peter"}
    {:id 456 :name "petra"}))))

)
