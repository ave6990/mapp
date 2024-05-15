(ns mapp.views.verifications
  (:require
    [mapp.lib.gen-html :as h]
    [mapp.views.view :as v]))

(def content
  (h/join
    (h/header
      v/main-menu)
    (h/main
      (h/section {:id "toolbar-panel"})
      (h/section {:id "table-panel"})
      (h/section {:id "edit-panel"}))
    (h/footer
      (h/p "Mapp, версия 2024-05-15"))))

(defn get-verifications
  [query]
  (v/gen-page
    "Журнал ПР"
    content))
