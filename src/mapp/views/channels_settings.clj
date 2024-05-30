(ns mapp.views.channels-settings)

(def toolbar-fields-settings
  '(["id" " id "]
   ["МП" " methodology_id "]
   ["компонент" " component LIKE '**'"]
   ["ед._изм." " units LIKE '**'"]))

(def fields-settings
  '([:id "ID" true]
    [:methodology_id "ID МП" true]
    [:channel "Наименование канала" true]
    [:component "Компонент" true]
    [:range_from "Диапазон от" true]
    [:range_to "Диапазон до" true]
    [:units "Единицы измерения" true]
    [:low_unit "ЕМР" true]
    [:view_range_from "Показания от" true]
    [:view_range_to "Показания до" true]
    [:comment "Комментарий" true]))

(def context-menu-settings
  '(["-" "-"]
    ["МХ" "ctx-menu-action-metrology"]))
