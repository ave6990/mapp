(ns mapp.controller.controller
  (:require
    [clojure.string :as string]
    [seesaw.core :refer :all]
    [seesaw.bind :as b]
    [seesaw.keymap :refer :all]
    [seesaw.value :refer :all]
    [seesaw.table :refer [table-model value-at update-at!]]
    [mapp.model.midb :as midb]
    [mapp.view.main :as v]
    [mapp.controller.main-menu :as m-menu]
    [mapp.controller.table-context-menu :as table-c-menu]))

(defn make-table-model
  "external deps:
    mapp.core.clj"
  [data column-settings]
  (table-model
    :columns (->> column-settings
                  (map :key)
                  vec)
    :rows data))

(defn table-mouse-clicked
  [column-settings]
  (fn [e]
    (let [root (to-frame e)
          tab (select root [:#v-table])
          edit-fields (concat (select root [:.record-editor])
                              (select root [:.record-info]))
          record (first
                   (value-at
                     tab
                     (selection tab {:multi? true})))]
      (dorun (map (fn [field]
               (value! field
                      ((config field :id) record)))
           edit-fields))
      (println record))))

(defn make-table-c-menu
  [menu fr]
  (config!
    (select fr [:#v-table])
    :popup
      menu)
  fr)

(defn insert-string
  [ss s pos]
  (string/replace
    (->>
      (list 
        (take pos ss)
        s
        (drop pos ss))
      (map string/join)
      string/join)
    #"/s+"
    " "))

(defn query-toolbar-button-handler
  [btn]
  (fn [e]
      (let [root (to-frame e)
            query-text (select root [:#query-text])]
        (value!
          query-text
          (insert-string
            (value query-text)
            (user-data btn)
            (config
              query-text
              :caret-position))))))

(defn prev-page-button-clicked
  [query-event-handler]
  (fn [e]
    (let [root (to-frame e)
          page-text (select root [:#page-text])
          v (read-string (value page-text))]
      (text!
        page-text
        (if (> v 1)
            (dec v)
            1)))
    (query-event-handler e)))

(defn calc-offset
  [page limit]
  (* (dec page) limit))

(defn calc-pages
  [records-count limit]
  (int (Math/ceil (/ records-count limit))))

(defn query-enter-pressed
  [e]
  (let [root (to-frame e)
        page-text (select root [:#page-text])]
    (value! page-text 1)))

(defn make-query-handler
  [fn-get-records column-settings root]
  (fn [e]
      (let [limit 100
            query (->>
                    (select root [:#query-text])
                    value)
            v-table (select root [:#v-table])
            pages-label (select root [:#pages-label])
            page-text (select root [:#page-text])
            group-by (->>
                       (select root [:.group-by])
                       (filter #(value %))
                       (map (fn [el]
                                (user-data el)))
                       (string/join ", "))
            #_order-by #_(->>
                       (select root [:#order-by])
                       value)
            records (fn-get-records 
                      query
                      limit
                      (calc-offset (read-string (value page-text)) limit)
                      group-by
                      #_order-by)
            data (:data records)
            records-count (:count records)]
        (config!
          v-table
          :model (make-table-model
                   data column-settings)
          :column-widths (->>
                           column-settings
                           (map :width)
                           vec))
        (text!
          pages-label
          (str
            (calc-pages records-count limit)
            " (" records-count ")")))))

(defn next-page-button-clicked
  [query-event-handler]
  (fn [e]
      (let [root (to-frame e)
            page-text (select root [:#page-text])
            pages-label (select root [:#pages-label])
            pages-count (read-string (first (string/split (value pages-label) #" ")))
            current-page (read-string (value page-text))]
        (text!
          page-text
          (if (< current-page pages-count)
              (inc current-page)
              pages-count)))
      (query-event-handler e)))

(defn read-fields
  [fields]
  (doall
    (reduce (fn [m field]
                (let [v (value field)]
                  (if (not= v "")
                      (assoc m (config field :id) v)
                      (assoc m (config field :id) nil))))
         {}
         fields)))

(defn make-write-fn
  [id data]
  (fn [e]
      (midb/write!
        id
        data)))

(defn save-button-clicked
  [e]
  (let [root (to-root e)
        btn (to-widget e)
        tab-id (user-data btn)
        m (read-fields (select root [:.record-editor]))]
    (when (not (empty? m))
          (->>
            (v/make-insert-dialog
              tab-id
              m
              make-write-fn)
            show!))))

(defn clear-button-clicked
  [e]
  (let [root (to-root e)
        edit-fields (select root [:.record-editor])]
    (dorun
      (map (fn [field]
               (text! field ""))
           edit-fields))))

(defn add-behavior
  [fn-get-records column-settings root]
  (let [query (select root [:#query-text])
        v-table (select root [:#v-table])
        status (select root [:#status-label])
        page-text (select root [:#page-text])
        prev-page-button (select root [:#prev-page-button])
        next-page-button (select root [:#next-page-button])
        save-button (select root [:#save-button])
        clear-button (select root [:#clear-button])
        cancel-button (select root [:#cancel-button])
        pages-label (select root [:#pages-label])
        query-handler (make-query-handler
                        fn-get-records
                        column-settings
                        root)]
    (doall
      (map (fn [btn]
               (listen
                 btn
                 :mouse-clicked
                 (query-toolbar-button-handler btn)))
           (select root [:.query-toolbar])))
    (listen
      page-text
      :action-performed
      query-handler)
    (listen
      prev-page-button
      :mouse-clicked
      (prev-page-button-clicked
        query-handler))
    (listen
      next-page-button
      :mouse-clicked
      (next-page-button-clicked
        query-handler))
    (when save-button
      (listen
        save-button
        :mouse-clicked
        save-button-clicked))
    (when clear-button
      (listen
        clear-button
        :mouse-clicked
        clear-button-clicked))
    #_(b/bind
      query
      status)
    (map-key query "ENTER"
      (fn [e]
        (query-enter-pressed e)
        (query-handler e)))
    (listen
      v-table
      :mouse-clicked
      (table-mouse-clicked column-settings))
    #_(listen
      root
      :window-activated
      query-handler)
    (map-key root "F5"
      query-handler)
    #_(b/bind
      upload
      (b/transform #(if %
                        "Фильтр включает выгруженные записи."
                        "Фильтр не включает выгруженные записи."))
      status)
  root))

;; Add a behavior to frames 
(doseq [[model-get column-settings context-menu fr]
        [[midb/get-conditions v/conditions-column-settings nil v/conditions-frame]
         [midb/get-gso v/gso-column-settings nil v/gso-frame]
         [midb/get-references v/references-column-settings nil v/references-frame]
         [midb/get-counteragents v/counteragents-column-settings nil v/counteragents-frame]
         [midb/get-operations v/operations-column-settings nil v/operations-frame]
         [midb/get-measurements v/measurements-column-settings nil v/measurements-frame]
         [midb/get-methodology v/methodology-column-settings nil v/methodology-frame]
         [midb/get-journal v/journal-column-settings nil v/journal-frame]
         [midb/get-set-verification-tools v/svt-column-settings nil v/svt-frame]]]
  ;; Add context menu to a table
  (when context-menu
        (make-table-c-menu context-menu fr))
  (add-behavior
    model-get
    column-settings
    fr))

;; TOFIX move to main_menu.clj
(def main-menu
  (v/make-main-menu
    [(menu :text "Главное"
           :items [m-menu/main-about-action m-menu/main-exit-action])
     (menu :text "Окна"
           :items 
             (vec
               (map (fn [fr]
                        (action
                          :handler (fn [e]
                                       (->>
                                         fr
                                         pack!
                                         show!))
                          :name (config fr :title)))
                    [v/gso-frame v/counteragents-frame
                     v/conditions-frame v/references-frame])))]))



(comment

(ns mapp.controller.controller)
(require '[mapp.view.main :as v] :reload)
(require '[mapp.controller.table-context-menu :as table-c-menu] :reload)
(require '[mapp.model.midb :as midb] :reload)

)
