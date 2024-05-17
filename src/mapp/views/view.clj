(ns mapp.views.view
  (:require
    [hiccup2.core :as h]
    [mapp.views.templates :as tmpl]))

(def verifications-page
  (tmpl/gen-page
    "Журнал ПР"
    tmpl/page-template))
