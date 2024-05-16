(ns mapp.view.counteragents-settings)

(def column-settings
  [[:id 50 nil]
   [:inn 100 nil]
   [:short_name 200 nil]
   [:name 350 nil]
   [:address 350 nil]
   [:type 100 nil]])

(def toolbar-fields-settings
  '(["id" " id "]
    ["наименование" " (short_name || ' ' || name) LIKE '%%' "]
    ["инн" " inn "]
    [" адрес " " address "]))

(def edit-panel-settings
  '(["id" :id ""]
    ["Наименование" :name ""]
    ["Краткое" :short_name ""]
    ["Адрес" :address ""]
    ["ИНН" :inn ""]
    ["Тип" :type ""]))
