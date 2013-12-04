(ns mklappstuhl.stock-utils.core
  (:require [taoensso.timbre :as timbre]
            [mklappstuhl.stock-utils.metrics :as metrics]
            [mklappstuhl.stock-utils.persistence :as pers]))

(defn -main [& args]
  (timbre/set-level! :debug)
  (timbre/set-config! [:appenders :spit :enabled?] true)
  (timbre/set-config! [:shared-appender-config :spit-filename] "logs/all.log")
  (pers/migrate-all pers/pg)
  (timbre/info "Goldman is coming to dig"))

(-main)
