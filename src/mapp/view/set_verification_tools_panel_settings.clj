(ns metrology.view.set-verification-tools-panel-settings)

(def column-settings
  [[:expiration 25 nil]
   [:v_id 50 nil]
   [:ref_id 50 nil]
   [:mi_name 150 nil]
   [:mi_type 150 nil]
   [:components 100 nil]
   [:value 100 nil]
   [:serial_number 100 nil]
   [:number_1c 75 nil]
   [:level 30 nil]
   [:expiration_date 75 nil]])

(def toolbar-fields-settings
  '(["id поверки" " v_id "]
    ["id эталона" " ref_id "]
    ["номер 1C" " number_1c "]))
