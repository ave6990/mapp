(ns mapp.views.operations-settings)

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
    ["v_id" " v_op.v_id "]
    ["МП" " op.methodology_id "]))

(def fields-settings
  '([:id "ID" true]
    [:op_id "id операции" true]
    [:v_id "id поверки" true]
    [:result "Соответствует" true]
    [:name "Операция" false]
    [:unusability "Причина непригодности" true]
    [:verification_type "Тип поверки" false]
    [:comment "Комментарий" false]))
