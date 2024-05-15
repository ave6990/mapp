(ns mapp.core
  (:gen-class)
  (:require 
    [clojure.string :as string]
    [seesaw.core :refer :all]
    [seesaw.bind :as b] ;;TOFIX delete, not use
    [seesaw.value :refer :all]
    [seesaw.keymap :refer :all] ;;TOFIX delete, not use
    [seesaw.dev :refer :all]  ;;NB TOFIX delete before release!
    [clojure.pprint :refer [pprint]]  ;;NB TOFIX delete before release!
    [mapp.lib.chemistry :as ch]
    [mapp.lib.metrology :as m]
    [mapp.lib.gs2000 :as gs]
    [mapp.model.midb :as midb]
    [mapp.view.main :as v]
    [mapp.controller.controller :as control]
    [mapp.controller.main-menu :as m-menu]
    [mapp.controller.table-context-menu :as table-c-menu]))

(ns mapp.core)
(require '[mapp.view.main :as v] :reload)
(require '[mapp.model.midb :as midb] :reload)
(require '[mapp.controller.controller :as control] :reload)
(require '[mapp.controller.table-context-menu :as table-c-menu] :reload)
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

(require '[mapp.controller.main-menu :as m-menu] :reload)

(require '[mapp.model.midb :as midb] :reload)

(require '[clojure.string :as string])

(require '[mapp.view.main :as v] :reload)

)
