(ns cljs.context-menu
  (:require
    [cljs.dom-functions :refer :all]))

(defn get-position
  [event]
    {:x (-> event .-pageX) 
     :y (-> event .-pageY)})

(defn activate
  []
  (add-class (get-by-id "context-menu")
             "context-menu-active"))

(defn hide
  []
  (remove-class (get-by-id "context-menu")
                "context-menu-active"))

(defn show
  [event]
  (let [menu (get-by-id "context-menu")
        pos (get-position event)]
    (-> event (.preventDefault))
    (activate)
    (set! (-> menu .-style .-left) (:x pos))
    (set! (-> menu .-style .-top) (:y pos))))

(defn item-click
  [menu-actions] 
  (fn [event]
      (let [id (-> event
                   .-target
                   (.getAttribute "name"))
            menu (get-by-id "context-menu")]
        (hide)
        ((menu-actions id)))))
