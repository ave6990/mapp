(ns mapp.views.metrology-settings)

(def toolbar-fields-settings
  '(["id" " metr.id "]
   ["id канала" " ch.id "]
   ["Канал" " (ch.channel || ' ' || ch.component) LIKE '**'"]
   ["Рег. №" " met.registry_number LIKE '**'"]))

(def fields-settings
  '([:id "ID" true]
    [:registry_number "Регистрационный номер" false]
    [:channel_id "ID канала" true]
    [:channel "Наименование канала" false]
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
