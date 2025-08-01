(defproject mapp "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :jvm-opts ["-Duser.timezone=UTC"]
  :dependencies
    [[org.clojure/clojure "1.11.1"]
     [org.clojure/java.jdbc "0.7.12"]
     [org.xerial/sqlite-jdbc "3.42.0.0"]
     [org.mariadb.jdbc/mariadb-java-client "1.1.5"]
     ;;[digest "1.4.10"] ;; MD5 hash
     [incanter "1.9.3"]
     [enlive "1.1.6"]
     [hiccup "2.0.0-RC3"]
     [compojure "1.7.1"]
     [ring/ring-core "1.7.1"]
     [ring/ring-jetty-adapter "1.7.1"]
     [ring/ring-json "0.5.1"]
     [ring/ring-defaults "0.3.2"]]
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler mapp.handler/app
         :port 3000
         :host "0.0.0.0"}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}})
