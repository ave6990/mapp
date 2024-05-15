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
    (h/style {:type "text/css"} "")
    (h/script {:type "test/javascript"} "")))

(defn href
  [path name]
  (h/a {:href path} name))

(defn table 
  [model data]
  ())

(def main-menu
  (h/nav {:id "main-menu"}
    (href "/" "Журнал ПР")
    (href "/gso" "ГСО")
    (href "/refs" "Эталоны")
    (href "/conditions" "Условия поверки")))

(defn gen-page
  [title content]
  (h/doctype
    (h/html
      (gen-page-head title)
      (h/body content))))
