(ns mklappstuhl.stock-utils.core
  (:require [mklappstuhl.stock-utils.metrics :as metrics]
            [mklappstuhl.stock-utils.simulate :as simulate]
            [mklappstuhl.stock-utils.util :as util]
            [mklappstuhl.stock-utils.populate :as populate]
            [mklappstuhl.stock-utils.persistence :as pers]))

(defn -main []
  (do
    (pers/migrate-all pers/pg)
    (populate/populate-stocks
     "./resources/short.tsv"
     \tab
     [:name :full_name]
     [:name :full_name])
    (populate/populate-days)))


