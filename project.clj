(defproject stock-utils "0.0.1"
  :description "analyze stock data by common metrics"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojars.tn1ck/yfinance "0.3.0-SNAPSHOT"]
                 [incanter  "1.5.4"]
                 [korma "0.3.0-RC5"]
                 [clj-time "0.6.0"]
                 [org.clojure/java.jdbc "0.3.0-alpha4"]
                 [postgresql/postgresql "9.1-901-1.jdbc4"]
                 [org.clojure/tools.namespace "0.2.4"]
                 [org.clojure/data.csv "0.1.2"]]
  :main mklappstuhl.stock-utils.core)
