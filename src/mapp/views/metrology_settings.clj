(ns mapp.views.metrology-settings)

(def toolbar-fields-settings
  '(["id" " id "]
   ["Канал" " channel_id "]))

(def fields-settings
  '([:id "ID" true]
    [:channel_id "ID канала" true]
    [:r_from "Диапазон от" true]
    [:r_to "Диапазон до" true]
    [:value "Значение" true]
    [:fraction "Доля" true]
    [:type_id "Тип МХ" true]
    [:name "Имя" true]
    [:units "Единица измерения" true]
    [:operation_id "Операция" true]
    [:text "Текст" true]
    [:comment "Комментарий" true]))
