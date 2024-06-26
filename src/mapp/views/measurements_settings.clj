(ns mapp.views.measurements-settings)

(def column-settings
  [[:id 50 nil]
   [:v_id 50 nil]
   [:serial_number 100 nil]
   [:metrology_id 75 nil]
   [:channel_name 150 nil]
   [:channel 150 nil]
   [:value 75 nil]
   [:value_2 75 nil]
   [:ref_value 75 nil]
   [:text 100 nil]
   [:error_value 75 nil]
   [:error_fraction 75 nil]
   [:error_units 75 nil]
   [:error_type 50 nil]
   [:comment 300 nil]])

(def toolbar-fields-settings
  '(["id" " meas.id "]
    ["v_id" " v.id "]
    ["зав. №" " v.serial_number LIKE '**'"]))

(def fields-settings
  '([:id "id" true]
    [:v_id "id поверки" true]
    [:operation_id "id операции" true]
    [:operation "Операция" false]
    [:serial_number "Зав. №" false]
    [:channel_name "Имя канала" true]
    [:channel "Канал" false]
    [:metrology_id "id МХ" true]
    [:value "Изм. значение" true]
    [:value_2 "Знач. (вариация)" true]
    [:ref_value "Действ. знач." true]
    [:ref_value_id "ID опорного значения" true]
    [:nominal_range "Опорное значение по НД" false]
    [:text "Текст" true]
    [:unusability "Причина непригодности" true]
    [:comment "Комментарий" true]))

(def context-menu-settings
  '(["-" "-"]
    ["Опорные значения" "ctx-menu-action-ref-values"]))
