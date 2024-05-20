(ns mapp.views.gso-settings)

(def column-settings
  [[:id 50 nil]
   [:number_1c 75 nil]
   [:type 75 nil]
   [:number 150 nil]
   [:level 50 nil]
   [:available 50 nil]
   [:components 150 nil]
   [:concentration 75 nil]
   [:uncertainity 75 nil]
   [:units 75 nil]
   [:pass_number 100 nil]
   [:date 100 nil]
   [:expiration_date 100 nil]
   [:metrology 300 nil]])

(def toolbar-fields-settings
  '(["id" " id "]
   ["№ паспорта" " pass_number "]
   ["дата" " date "]
   ["компонент" " components "]
   ["концентрация %" " conc "]
   ["номер 1С" " number_1c "]
   ["рег. №" " number "]
   ["срок годности" " expiration_date "]))

(def edit-panel-settings
  '(["id" :id ""]
    ["1С №" :number_1c ""]
    ["Тип" :type ""]
    ["Рег. №" :number ""]
    ["Разряд" :level ""]
    ["Наличие" :available ""]
    ["Компоненты" :components ""]
    ["Концентрация" :concentration ""]
    ["Неопределенность" :uncertainity ""]
    ["Ед. изм." :units ""]
    ["Документ" :document ""]
    ["Паспорт №" :pass_number ""]
    ["Дата получения" :date ""]
    ["Дата изготовления" :manufacture_date ""]
    ["Срок годности" :expiration_date ""]
    ["Баллон №" :cylinder_number ""]
    ["Объем" :volume ""]
    ["Давление" :pressure ""]))
