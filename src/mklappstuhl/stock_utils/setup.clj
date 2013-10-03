(ns mklappstuhl.stock-utils.setup
  (:require [mklappstuhl.stock-utils.persistence :as pers]
            [mklappstuhl.stock-utils.util :as util]
            [mklappstuhl.stock-utils.populate :as populate]))


(defn setup-dev! []
  "loads some stocks into the db and loads their day-trade data into the database"
  (do
    (pers/drop-schema pers/pg)
    (pers/migrate-all pers/pg)
    (map #(populate/populate-stocks % \tab [:name :full_name] [:name :full_name])
          (util/full-directory-list "./resources/symbol-lists/"))
    (populate/populate-days)))


; (setup-dev!)
