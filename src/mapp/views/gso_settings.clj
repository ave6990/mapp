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

(def fields-settings
  '([:id "id" true]
    [:number_1c "1С №" true]
    [:type "Тип" true]
    [:number "Регистрационный №" true]
    [:level "Разряд" true]
    [:available "Наличие" true]
    [:components "Компоненты" true]
    [:concentration "Концентрация" true]
    [:uncertainity "Неопределенность" true]
    [:units "Ед. изм." true]
    [:document "Документ" true]
    [:pass_number "Паспорт №" true]
    [:date "Дата получения" true]
    [:manufacture_date "Дата изготовления" true]
    [:expiration_date "Срок годности" true]
    [:cylinder_number "Баллон №" true]
    [:volume "Объем" true]
    [:pressure "Давление" true]))
