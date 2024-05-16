(ns mapp.view.operations-settings)

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

(def edit-panel-settings
  '(["id" :id true]
    ["id операции" :op_id true]
    ["id поверки" :v_id true]
    ["Соответствует" :result true]
    ["Операция" :name false]
    ["Причина" :unusability true]
    ["Тип поверки" :verification_type false]
    ["Комментарий" :comment false]))
