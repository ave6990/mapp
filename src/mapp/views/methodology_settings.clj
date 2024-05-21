(ns mapp.views.methodology-settings)

(def column-settings
  [[:id 75 nil]
   [:registry_number 75 nil]
   [:short_name 200 nil]
   [:name 350 nil]
   [:approved 75 nil]
   [:mi_name 200 nil]
   [:date_from 100 nil]
   [:date_to 100 nil]
   [:temperature 75 nil]
   [:humidity 75 nil]
   [:pressure 75 nil]
   [:voltage 75 nil]
   [:frequency 75 nil]
   [:other 100 nil]
   [:limited 50 nil]
   [:mi_types 250 nil]
   [:comment 200 nil]])

(def toolbar-fields-settings
  '(["id" " id "]
    ["Рег. №" " registry_number "]
    ["Тип СИ" " mi_name || ' ' || mi_types like '**'"]))

(def fields-settings
  '([:id "id" true]
    [:registry_number "Рег. №" true]
    [:short_name "Краткое наим." true]
    [:name "Наименование" true]
    [:approved "Приказ" true]
    [:mi_name "Наименование СИ" true]
    [:date_from "Дата утв." true]
    [:date_to "Срок действия" true]
    [:temperature "Температура" true]
    [:humidity "Влажность" true]
    [:pressure "Давление" true]
    [:voltage "Напряжение" true]
    [:frequency "Частота" true]
    [:other "Прочие условия" true]
    [:limited "Сокращ. объем" true]
    [:mi_types "Типы СИ" true]
    [:comment "Комментарии" true]))

