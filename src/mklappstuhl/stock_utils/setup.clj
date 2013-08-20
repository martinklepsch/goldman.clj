(ns mklappstuhl.stock-utils.setup
  (:require [mklappstuhl.stock_utils.persistence :as persistence]
            [clojure.java.jdbc :as jdbc]))


(defn create-stock_symbols
  "creates the table stocks in the database"
  []
  (jdbc/create-table
   :stock_symbol
   [:id :serial "PRIMARY KEY"]
   [:name "varchar(7)"]  ;; longest stock-name is 5 characters long
   [:full_name "varchar(255)"]
   [:launch_date :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   ))

(defn create-stocks
  "creates the table stocks in the database"
  []
  (jdbc/create-table
   :stock
   [:id :serial "PRIMARY KEY"]
   [:trading_date :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"] ;d2
   ;; TODO: what do we want in the db? ask/bid/volume...
   [:stock_symbol_id :serial "references stock_symbol (id)"] ;; foreign key ;s
   [:high :integer "NOT NULL"] ;h
   [:low :integer "NOT NULL"] ;g
   [:open :integer "NOT NULL"] ;o
   [:close :integer "NOT NULL"] ; previous day - p
   [:volume :integer "NOT NULL"] ;v
   [:ask :integer "NOT NULL"] ;a
   [:bid :integer "NOT NULL"] ;b
   ))

(defn create-tables!
  "creates the necessary tables (stock,stock_symbol)"
  []
 (jdbc/with-connection
   persistence/pg
   (jdbc/transaction
     (create-stock_symbols)
     (create-stocks))))
