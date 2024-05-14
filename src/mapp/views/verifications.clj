(ns mapp.views.verifications
  (:require
    [mapp.lib.gen-html :as h]
    [mapp.model.midb :as m]
    [mapp.views.view :as v]))

(def content
  (h/join
    (h/header
      v/main-menu)
    (h/main
      (h/p "Журнал поверочных работ изменен"))
    (h/footer)))

(defn page
  []
  (v/gen-page
    "Журнал ПР"
    content))
