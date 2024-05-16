(ns mapp.view.measurements-settings)

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
    ["зав. №" " v.serial_number "]))

(def edit-panel-settings
  '(["id" :id true]
    ["id поверки" :v_id true]
    ["Зав. №" :serial_number false]
    ["Имя канала" :channel_name false]
    ["Канал" :channel false]
    ["id МХ" :metrology_id true]
    ["Изм. значение" :value true]
    ["Знач. (вариация)" :value_2 true]
    ["Действ. знач." :ref_value true]
    ["Текст" :text true]
    ["Комментарий" :comment true]))
