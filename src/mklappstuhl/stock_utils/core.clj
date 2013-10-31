(ns mklappstuhl.stock-utils.core
  (:require [liberator.core :refer [resource defresource]]
            [ring.adapter.jetty :refer [run-jetty]]
            [clojure.data.json :as json]
            [taoensso.timbre :as timbre]
            [compojure.core :refer [defroutes ANY GET]]
            [mklappstuhl.stock-utils.metrics :as metrics]
            [mklappstuhl.stock-utils.persistence :as pers]))

(defroutes app
  (GET ["/stocks" ] []
       (resource :available-media-types ["application/json"]
                 :handle-ok (fn [ctx]
                              (json/write-str (metrics/get-stocks)))))
  (GET ["/days/:stock" :stock #".*"] [stock]
       (resource :available-media-types ["application/json"]
                 :handle-ok (fn [ctx]
                              (json/write-str
                               (metrics/stock-data->nvd3
                                (metrics/get-stock-data (keyword stock))))))))

(defn -main [& args]
  (timbre/set-level! :debug)
  (timbre/set-config! [:appenders :spit :enabled?] true)
  (timbre/set-config! [:shared-appender-config :spit-filename] "logs/all.log")
  (pers/migrate-all pers/pg)
  (timbre/info "Goldman is coming to dig"))
  ; (run-jetty #'app {:port 3000}))

(-main)
