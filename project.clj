(defproject stock-utils "0.0.1"
  :description "analyze stock data by common metrics"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [yfinance "0.2.0"]
                 [incanter  "1.3.0"]
                 [korma "0.3.0-RC5"]
                 [org.clojars.jwhitlark/clj-time "0.4.5-SNAPSHOT"]
                 [org.clojure/java.jdbc "0.3.0-alpha4"]
                 [postgresql/postgresql "9.1-901.jdbc4"]
                 [org.clojure/data.csv "0.1.2"]]
  :main mklappstuhl.stock-utils.core)
