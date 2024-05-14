(ns metrology.lib.gen-html
  (:require [clojure.string :as string]))

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

(defn doctype
  "<!doctype html>"
  [& xs]
  (str "<!doctype html>\n" (string/join "\n" xs)))

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
