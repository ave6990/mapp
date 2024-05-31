(ns mapp.views.verification-operations-settings)

(def column-settings
  [[:id 50 nil]
   [:op_id 50 nil]
   [:v_id 50 nil]
   [:result 50 nil]
   [:section 50 nil]
   [:name 350 nil]
   [:unusability 350 nil]
   [:verification_type 50 nil]
   [:comment 150 nil]])

(def toolbar-fields-settings
  '(["id" " op.id "]
    ["МП" " op.methodology_id "]
    ["Наименование" " op.name LIKE '**'"]
    ["Рег. №" " met.registry_number LIKE '**'"]
    ["Типы СИ" " met.mi_types LIKE '**'"]))

(def fields-settings
  '([:id "ID" true]
    [:methodology_id "ID МП" true]
    [:registry_number "Регистрационный номер" false]
    [:short_name "Методика поверки" false]
    [:section "Номер пункта" true]
    [:name "Наименование операции" true]
    [:verification_type "Вид поверки" true]
    [:comment "Комментарий" true]
    [:info "Дополнительная информация" true]
    [:mi_types "Типы СИ" false]))

