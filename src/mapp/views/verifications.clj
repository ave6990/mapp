(ns mapp.views.verifications
  (:require
    [mapp.lib.gen-html :as h]
    [mapp.views.templates :as tmpl]))

(def verifications-page
  (tmpl/gen-page
    "Журнал ПР"
    tmpl/page-template))
