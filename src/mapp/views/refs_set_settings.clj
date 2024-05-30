(ns mapp.views.refs-set-settings)

(def fields-settings
  [[:expiration "Срок годности" false]
   [:v_id "ID поверки" true]
   [:id "ID записи" true]
   [:ref_id "ID эталона" true]
   [:type_id "Вид средства поверки" true]
   [:mi_name "Наименование СИ" false]
   [:mi_type "Тип, модификация СИ" false]
   [:components "Состав" false]
   [:value "Значение" false]
   [:serial_number "Заводской номер" false]
   [:number_1c "Номер 1С" false]
   [:level "Разряд" false]
   [:expiration_date "Срок годности" false]])

(def toolbar-fields-settings
  '(["id поверки" " v_id "]
    ["id эталона" " ref_id "]
    ["номер 1C" " number_1c LIKE '**'"]))
