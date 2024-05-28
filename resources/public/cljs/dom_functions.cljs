(ns cljs.dom-functions)

(defn get-by-id
  [id]
  (.getElementById js/document id))

(defn get-by-class
  [class-name]
  (seq (.getElementsByClassName js/document class-name)))

(defn get-by-tag
  [tag-name]
  (seq (.getElementsByTagName js/document tag-name)))

(defn get-value
  [id]
  (-> id get-by-id .-value))

(defn set-value
  [id v]
  (set! (-> id get-by-id .-value) v))

(defn get-html
  [id]
  (-> id get-by-id .-innerHTML))

(defn set-html
  [id v]
  (set! (-> id get-by-id .-innerHTML) v))

(defn add-event-listener
  [id event fn]
  (-> id get-by-id (.addEventListener event fn)))

(defn add-class
  [el cls]
  (-> el .-classList (.add cls)))

(defn remove-class
  [el cls]
  (-> el .-classList (.remove cls)))

(defn contains-class
  [el cls]
  (-> el .-classList (.contains cls)))

(defn toggle-class
  [el cls]
  (if (contains-class el cls)
      (remove-class el cls)
      (add-class el cls)))

(defn stringify
  [data]
  (.stringify js/JSON
              (clj->js data)))


