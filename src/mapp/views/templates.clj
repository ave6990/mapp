(ns mapp.views.templates
  (:require
    [mapp.lib.gen-html :as h]
    [mapp.views.verifications-settings :as vs]))

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
    (h/script {:crossorigin "false"
               :src "https://unpkg.com/react@17/umd/react.production.min.js"})
    (h/script {:crossorigin "false"
               :src "https://unpkg.com/react-dom@17/umd/react-dom.production.min.js"})
    (h/script {:src "https://cdn.jsdelivr.net/npm/scittle@0.6.17/dist/scittle.js"
               :type "application/javascript"} "")
    (h/script {:src "https://cdn.jsdelivr.net/npm/scittle@0.6.17/dist/scittle.reagent.js"
               :type "application/javascript"})
    (h/script {:src "https://cdn.jsdelivr.net/npm/scittle@0.6.17/dist/scittle.cljs-ajax.js"
               :type "application/javascript"})
    (h/script {:src "/cljs/main.cljs"
               :type "application/x-scittle"})))

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

(def footer
  (h/footer
      (h/p "Mapp, версия 2024-05-15")))

(def page-template 
  (h/join
    (h/header
      main-menu)
    (h/main
      (h/section {:id "toolbar-panel"})
      (h/section {:id "query-panel"}
        query-panel)
      (h/section {:id "table-panel"}
        "{table}")
      (h/section {:id "edit-panel"}))
    footer))
