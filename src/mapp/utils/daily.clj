(ns mapp.utils.daily
  (:require
    [clojure.string :as string]
    [clojure.java.shell :refer [sh]]
    [mapp.lib.gen-html :refer :all]))

(def midb-path
  ;"/mnt/d/UserData/YandexDisk/Ermolaev/midb/"
  "/media/sf_YandexDisk/Ermolaev/midb/")

(def styles
"html {
  font-family: sans-serif;
  font-size: 12pt;
}
body {
  margin: 0 0 0 1cm;
}
section {
  margin: 0;
  position: relative;
  height: 100%;
  page-break-after: always;
}
.verification {
  transform: rotate(90deg); margin: 0cm 1cm 0cm 0cm;
}
header {
  margin: 0 0 10pt 0;
  text-align: center;
}
table {
  margin: 0;
  width: 100%;
  border-collapse: collapse;
  border: 1px solid;
  line-height: 0.9;
}
td, th {
  width: 50%;
  height: 0.45cm;
  border: 1px solid;
  border-color: gray;
}
.verification > table {
  width: 160%;
}
.verification > h1 {
  width: 160%;
}
.info {
  width: 95%;
}
.result {
  width: 5%;
}
#p1 {
  position: absolute;
  top: 2.75cm;
  left: 0cm;
}
#p2 {
  position: absolute;
  top: 9.15cm;
  left: 0cm;
}
#p3 {
  position: absolute;
  top: 18.75cm;
  left: 0cm;
}
#p4 {
  position: absolute;
  top: 25.60cm;
  left: 0cm;
}
@media print {
  @page {
    margin: 0.5cm 1cm 0.5cm 1cm;
  }
}
")

#_(defn pages
  []
  (article
    (section {:class "daily"}
      (header
        (h1 "Дата: ______________________________________________"))
      (hr)
      (main
        (table
          (tr
            (td
              (table {:class "task_table"}
                (string/join "\n"
                             (map (fn [i] (tr (td " ")
                                              (td)))
                               (range 16))))))
          (tr
             (td
              (table {:class "task_table"}
                (string/join "\n"
                             (map (fn [i] (tr (td " ")
                                              (td )))
                               (range 16))))))))
        (hr)
        (table
          (string/join "\n"
                       (map (fn [i] (tr (td " ")))
                         (range 15)))))
    (section {:class "verification"}
      (h1 "Дата поверки ______________; T = _____ °C; M = _____ %; P = _____ кПа.")
      (table {:class "task_table"}
             (string/join "\n"
                          (map (fn [i] (tr (td {:class "info"} " ")
                                           (td {:class "result"} )))
                               (range 30)))))))

(defn pages
  []
  (article
    (section {:class "daily"}
      (main
        (table #_{:class "task_table"}
          (->>
            (range 53)
            (map (fn [i]
                     (tr
                        (td " ")
                        (td))))
            (string/join "\n")))))
    (section {:class "verification"}
      (h1 " ")
      (table {:class "task_table"}
             (string/join "\n"
                          (map (fn [i] (tr (td {:class "info"} " ")
                                           (td {:class "result"} )))
                               (range 32)))))))


(defn gen-daily
  "генерирует страницы ежедневника в файл daily.html."
  []
   (spit
     (str midb-path
          "daily.html")
     (doctype
      (html
        (head
          (meta {:charset "utf-8"})
          (meta {:name "author" :content "Aleksandr Ermolaev"})
          (meta {:name "e-mail" :content "ave6990@ya.ru"})
          (meta {:name "version" :content "2024-02-19"})
          (title "Daily planer")
          (style {:type "text/css"} styles)
          )
        (body
          (span {:id "p1"} "¤")
          (span {:id "p2"} "¤")
          (span {:id "p3"} "¤")
          (span {:id "p4"} "¤")
          (header)
          (main
            (pages)))))))

(map #(* 29.7 (/ %1 21))
     '(2.6 7.2 13.6 18.1))

(gen-daily)
(sh "vivaldi" (str midb-path "daily.html"))
