(ns metrology.view.methodology-panel-settings)

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
    ["Тип СИ" " mi_name || ' ' || mi_types like '%%'"]))

(def edit-panel-settings
  '(["id" :id ""]
    ["Рег. №" :registry_number ""]
    ["Краткое наим." :short_name ""]
    ["Наименование" :name ""]
    ["Приказ" :approved ""]
    ["Наименование СИ" :mi_name ""]
    ["Дата утв." :date_from ""]
    ["Срок действия" :date_to ""]
    ["Температура" :temperature ""]
    ["Влажность" :humidity ""]
    ["Давление" :pressure ""]
    ["Напряжение" :voltage ""]
    ["Частота" :frequency ""]
    ["Прочие условия" :other ""]
    ["Сокращ. объем" :limited ""]
    ["Типы СИ" :mi_types ""]
    ["Комментарии" :comment ""]))

