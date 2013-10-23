(ns mklappstuhl.stock-utils-test
  (:require [mklappstuhl.stock-utils.metrics :as metrics]
            [mklappstuhl.stock-utils.simulate :as simulate]
            [mklappstuhl.stock-utils.util :as util]
            [korma.core :as k]
            [korma.db :as kdb]
            [mklappstuhl.stock-utils.persistence :as pers]
            [mklappstuhl.stock-utils.populate :as populate] :reload-all)
  (:use [clojure.test]))

(def test-pg (kdb/postgres {:db "goldman_test"
                            :user "goldman"
                            :password ""
                            :host "localhost"
                            :port "5432"}))
(kdb/defdb test-kdb test-pg)

;; create the database goldman_test with the user goldman to run these tests
(deftest populate-stocks-single
    (do
      (pers/migrate-all test-pg)
      (is (= [{:industry nil, :sector nil, :full_name "Asia Broadband Inc", :name "AABB"}]
             (populate/populate-stocks
              "./resources/short.tsv"
              \tab
              [:name :full_name]
              [:name :full_name])))
      (pers/drop-schema test-pg)))
