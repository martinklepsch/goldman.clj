(ns mklappstuhl.stock-utils.core
  (:require [mklappstuhl.stock-utils.data :as data]
            [mklappstuhl.stock-utils.metrics :as metrics]
            [mklappstuhl.stock-utils.simulate :as simulate]
            [mklappstuhl.stock-utils.util :as util]
            [mklappstuhl.stock-utils.populate :as populate]
            [mklappstuhl.stock-utils.db :as db]))

(defn -main []
  (do (db/migrate-all)
      (populate/populate-stocks "./resources/nasdaq.csv")
      (populate/populate-stocks "./resources/nyse.csv")
      (populate/populate-stocks "./resources/amex.csv")))
