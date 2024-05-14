(defproject mapp "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies
    [[org.clojure/clojure "1.11.1"]
     [org.clojure/java.jdbc "0.7.12"]
     [org.xerial/sqlite-jdbc "3.42.0.0"]
     [compojure "1.7.1"]
     [ring/ring-core "1.7.1"]
     [ring/ring-jetty-adapter "1.7.1"]
     [ring/ring-defaults "0.3.2"]]
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler mapp.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}})
