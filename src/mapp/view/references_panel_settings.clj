(ns metrology.view.references-panel-settings)

(def column-settings
  [[:id 50 nil]
   [:number_1c 75 nil]
   [:mi_type 150 nil]
   [:mi_name 200 nil]
   [:serial_number 150 nil]
   [:manufacture_year 75 nil]
   [:registry_number 150 nil]
   [:code_fif 150 nil]
   [:expiration_date 100 nil]
   [:metrology 300 nil]])

(def toolbar-fields-settings
  '(["id" " id "]
   ["дата_изготовления" " manufacture_year "]
   ["зав. №" " serial_number "]
   ["номер 1С" " number_1c "]
   ["отдел" " department "]
   ["рег. №" " registry_number "]
   ["срок годности" " expiration_date "]
   ["тип СИ" " mi_type || ' ' || mi_name LIKE '%%' "]
   ["№ ФИФ" " code_fif "]))
