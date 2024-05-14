(ns metrology.core
  (:gen-class)
  (:require 
    [clojure.string :as string]
    [seesaw.core :refer :all]
    [seesaw.bind :as b] ;;TOFIX delete, not use
    [seesaw.value :refer :all]
    [seesaw.keymap :refer :all] ;;TOFIX delete, not use
    [seesaw.dev :refer :all]  ;;NB TOFIX delete before release!
    [clojure.pprint :refer [pprint]]  ;;NB TOFIX delete before release!
    [metrology.lib.chemistry :as ch]
    [metrology.lib.metrology :as m]
    [metrology.lib.gs2000 :as gs]
    [metrology.model.midb :as midb]
    [metrology.view.main :as v]
    [metrology.controller.controller :as control]
    [metrology.controller.main-menu :as m-menu]
    [metrology.controller.table-context-menu :as table-c-menu]))

(ns metrology.core)
(require '[metrology.view.main :as v] :reload)
(require '[metrology.model.midb :as midb] :reload)
(require '[metrology.controller.controller :as control] :reload)
(require '[metrology.controller.table-context-menu :as table-c-menu] :reload)
(def main-frame
  (->>
    (v/make-frame
      :verifications
      "MIdb v.0.0.1"
      control/main-menu
      v/verifications-table-panel)
    (control/make-table-c-menu table-c-menu/verifications-table-menu)
    (control/add-behavior
      midb/get-verifications
      v/verifications-column-settings)))
(defn -main
  ""
  [& args]
  (->>
    main-frame
    pack!
    show!))

(-main)



(comment

(midb/copy-record! 4290 1)

(midb/delete-record! 4292)

(frame :title "Hello Swing"
       :on-close :exit
       :content (w/button :text "Click Me"
                          :listen [:action handler]))

(:text (text :text "hello"))

(source text)

(find-doc "table-panel")

(dir string)

(doc reduce)

(doc seesaw.table/value-at)

(source seesaw.table/value-at)

(show-options (text))

(seesaw.event/events-for (table))

(show-events (frame))

(require '[seesaw.widget-options :as w-opt])

(require '[metrology.controller.main-menu :as m-menu] :reload)

(require '[metrology.model.midb :as midb] :reload)

(require '[clojure.string :as string])

(require '[metrology.view.main :as v] :reload)

)
