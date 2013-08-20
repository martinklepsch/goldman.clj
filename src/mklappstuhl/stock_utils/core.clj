(ns mklappstuhl.stock-utils.core
  (:require [mklappstuhl.stock-utils.data :as data]
            [mklappstuhl.stock-utils.metrics :as metrics]
            [mklappstuhl.stock-utils.simulate :as simulate]
            [mklappstuhl.stock-utils.util :as util]
            [mklappstuhl.stock-utils.setup :as setup]))

(defn -main []
  (setup/create-tables!))
