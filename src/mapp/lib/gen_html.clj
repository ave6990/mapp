(ns mapp.lib.gen-html
  (:require
    [clojure.string :as string]
    [clojure.math :as math]))

(defn join
  [& xs]
  (string/join "\n " xs))

(defn indent
  "Добавить отступ к строкам."
  [s]
  (str "  " (string/join "\n  " (string/split s #"\n"))))

(defn set-attributes
 "Преобразует hash-map в строку с аттрибутами html-тэгов."
  [m]
  (reduce (fn [s [k v]]
              (str s " "
                   (string/replace (str k) ":" "")
                   "=\"" v "\""))
          ""
          m))

(defmacro html-tag
  [tag]
  (let [s (gensym "s")]
    `(defn ~(symbol tag)
       ([& ~s]
        (string/join "\n" 
                     (list 
                       (str ~(str \< tag)
                            (if (map? (first ~s))
                                (set-attributes (first ~s))
                                "")
                            ~(str \>))
                       (indent (string/join "\n" (if (map? (first ~s))
                                                     (rest ~s)
                                                     ~s)))
                       ~(str \< \/ tag \>))))
       ([]
        (~tag "")))))

(defmacro html-tag-unpaired
  "Unpaired tag with an attributes."
  [tag]
  (let [s (gensym "s")
        k (gensym "k")
        v (gensym "v")
        m (gensym "m")]
    `(defn ~tag
       ~(str "String representation of html tag _" tag "_ with an atributes.")
       ([~m]
       (str ~(str "<" tag)
            (reduce (fn [~s [~k ~v]]
                      (str ~s " "
                           (string/replace ~k ":" "")
                           "=\"" ~v "\""))
                    ""
                    ~m)
           ">"))
       ([]
        ~(str "<" tag ">")))))

(html-tag html)
(html-tag head)
(html-tag title)
(html-tag style)
(html-tag script)
(html-tag body)
(html-tag h1)
(html-tag h2)
(html-tag h3)
(html-tag section)
(html-tag div)
(html-tag header)
(html-tag main)
(html-tag footer)
(html-tag nav)
(html-tag details)
(html-tag summary)
(html-tag table)
(html-tag thead)
(html-tag tbody)
(html-tag tr)
(html-tag th)
(html-tag td)
(html-tag p)
(html-tag time)
(html-tag strong)
(html-tag ul)
(html-tag ol)
(html-tag li)
(html-tag em)
(html-tag span)
(html-tag a)
(html-tag article)
(html-tag-unpaired meta)
(html-tag-unpaired br)
(html-tag-unpaired img)
(html-tag-unpaired input)
(html-tag-unpaired hr)
(html-tag-unpaired link)

(defn doctype
  "<!doctype html>"
  [& xs]
  (str "<!doctype html>\n" (string/join "\n" xs)))

(defn ^:private prepare-mask
  [vs mask]
  (let [c (->>
           (/ (count vs) (count mask))
           math/ceil
           math/round)]
    (repeat c mask)))

(defn create-table-header
  [model]
  (tr
    (string/join "\n"
      (map (fn [[id nm _]]
               (th {:class (doall (str "col" id))}
                   nm))
           model))))

(defn create-table-row
  [model row-data]
  (let [ids (map (fn [[id _ _]] id) model)
        editables (map (fn [[_ _ editable]] editable) model)]
    (tr 
      (string/join "\n"
        (map (fn [id editable]
                 (td {:class (doall (str "col" id))
                      :contenteditable editable}
                   (id row-data)))
             ids
             editables)))))

(defn create-table-rows
  [model data]
    (string/join "\n"
      (map (fn [row]
               (create-table-row model row))
           data)))

(defn create-table
  "args: model [[id name visibility] [] ... []]"
  [id model data]
  (table {:id id}
    (thead
      (create-table-header model))
    (tbody
      (create-table-rows model data))))

(defn formated-table-cells
  "args:
    `vs   двумерное множество значений
    `mask множество векторов значений [:rowspan :colspan] описывающих
          структуру строки таблицы.
   example:
    (table-cells
      th
      (list
        (list \"Детектор\" \"Значение уровня шумов\" \"Значение дрейфа\")
        (list \"действительное\" \"допускаемое\" \"ед. изм.\"
              \"действительное\" \"допускаемое\" \"ед. изм.\"))
      (list
        (list [2 1] [1 3] [1 3])
        (list [1 1] [1 1] [1 1] [1 1] [1 1] [1 1])))
    used:
      mapp.protocols.custom"
  ([column-fn vs mask ]
    (->>
      (map (fn [row sp]
               (->>
                 (map (fn [v [rspan cspan]]
                          (column-fn {:rowspan rspan
                                      :colspan cspan}
                                     v))
                      row
                      sp)
                 (string/join "\n")
                 tr))
           vs
           (prepare-mask vs mask))
      (string/join "\n")))
  ([column-fn vs]
    (formated-table-cells
      column-fn
      vs
      (doall
        (map (fn [r]
                 (map (fn [_]
                          [1 1])
                      r))
             vs)))))

(defn formated-table-header
  "used:
    mapp.protocols.custom"
  ([vs mask]
    (formated-table-cells th vs mask))
  ([vs]
    (formated-table-cells th vs)))

(defn formated-table-rows
  "used:
    mapp.protocols.custom"
  ([vs mask]
    (formated-table-cells td vs mask))
  ([vs]
    (formated-table-cells td vs)))

(comment

(spit "/media/sf_YandexDisk/Ermolaev/midb/protocol.html"
      (html
        (head)
        (body
          (section
            (header)
            (main
              (p {:id "greeting"} "Hello!"))
            (footer)))))

(require '[clojure.repl :refer :all])

(require '[clojure.string :as string])

(require '[clojure.pprint :refer [pprint]])

(doc time)

)
