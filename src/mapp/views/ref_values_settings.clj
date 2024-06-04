(ns mapp.views.ref-values-settings)

(def toolbar-fields-settings
  '(["ID" " rv.id "]
    ["Рег. №" " met.registry_number LIKE '**' "]))

(def fields-settings
  '([:id "id" true]
    [:registry_number "Регистрационный номер" false]
    [:channel_name "Имя канала" false]
    [:channel "Канал" false]
    [:range "Диапазон" false]
    [:metrology_id "id МХ" true]
    [:number "Номер п/п" true]
    [:nominal_range "Диапазон допустимых значений" false]
    [:nominal "Номинальное значение" true]
    [:units "Единицы измерения" true]
    [:tolerance "Отклонение" true]
    [:tolerance_type "Тип отклонения" true]
    [:reference "Эталон" true]
    [:level "Разряд, класс" true]
    [:error "Значение МХ" true]
    [:error_type "Тип МХ" true]
    [:comment "Комментарий" true]))
