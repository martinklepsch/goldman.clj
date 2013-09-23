(ns mklappstuhl.stock-utils.core
  (:require [liberator.core :refer [resource defresource]]
            [ring.adapter.jetty :refer [run-jetty]]
            [clojure.data.json :as json]
            [compojure.core :refer [defroutes ANY GET]]
            [mklappstuhl.stock-utils.metrics :as metrics]))

(defroutes app
  (GET ["/stock/:stock" :stock #".*"] [stock]
       (resource :available-media-types ["application/json"]
                 :handle-ok (fn [ctx]
                              (json/write-str (metrics/get-stock-data (keyword stock)))))))

(defn -main [& args]
  (run-jetty #'app {:port 3000}))
