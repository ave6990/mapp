(ns mapp.views.counteragents-settings)

(def column-settings
  [[:id 50 nil]
   [:inn 100 nil]
   [:short_name 200 nil]
   [:name 350 nil]
   [:address 350 nil]
   [:type 100 nil]])

(def toolbar-fields-settings
  '(["id" " id "]
    ["наименование" " concat(short_name, ' ', name) LIKE '**' "]
    ["инн" " inn LIKE '**'"]
    ["адрес" " address LIKE '**'"]))

(def fields-settings
  '([:id "id" true]
    [:name "Наименование" true]
    [:short_name "Краткое" true]
    [:address "Адрес" true]
    [:inn "ИНН" true]
    [:type "Тип" true]))
