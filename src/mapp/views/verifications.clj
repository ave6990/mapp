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
      (h/section {:id "table-panel"}
        v/query-panel)
      (h/section {:id "edit-panel"}))
    (h/footer
      (h/p "Mapp, версия 2024-05-15"))))

(def verifications-page
  (v/gen-page
    "Журнал ПР"
    content))
