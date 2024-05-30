(ns mapp.views.conditions-settings)

(def column-settings
  [[:id 50 nil]
   [:date 100 nil]
   [:temperature 75 nil]
   [:humidity 75 nil]
   [:pressure 75 nil]
   [:voltage 75 nil]
   [:frequency 75 nil]
   [:other 350 nil]
   [:location 350 nil]
   [:comment 350 nil]])

(def toolbar-fields-settings
  '(["id" " id "]
    ["дата" " date "]
    ["локация" " location LIKE '**'"]
    ["комментарий" " comment LIKE '**'"]))

(def fields-settings
  '([:id "id" true]
    [:date "Дата" true]
    [:temperature "Температура, °C" true]
    [:humidity "Влажность, %" true]
    [:pressure "Давление, кПа" true]
    [:voltage "Напряжение, В" true]
    [:frequency "Частота, Гц" true]
    [:location "Место" true]
    [:comment "Комментарий" true]))
