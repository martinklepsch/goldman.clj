(ns mklappstuhl.stock-utils.db
  (:require [korma.db :as kdb]
            [korma.core :as k]
            [clojure.java.jdbc :as jdbc]))


(def pg (kdb/postgres {:db "goldman"
                      :user "goldman"
                      :password ""}))

(defn create-stock_symbols
  "creates the table stocks in the database"
  []
  (jdbc/create-table
   :stock_symbols
   [:id :serial "PRIMARY KEY"]
   [:name "varchar(15)"]  ;; longest stock-name is 5 characters long
   [:full_name "varchar(255)"]
   [:sector "varchar(255)"]
   [:industry "varchar(255)"]
   ))

; should be called trading-days?
(defn create-stocks
  "creates the table stocks in the database"
  []
  (jdbc/create-table
   :stock
   [:id :serial "PRIMARY KEY"]
   ; maybe that field should be just called date?
   ; also it should just be a date and no hours, minutes, whatsoever
   ; and it should be unique
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

(defn persist-day [sym day-data]
  (jdbc/insert! pg :stock day-data))

(defn create-tables!
  "creates the necessary tables (stock,stock_symbol)"
  []
 (jdbc/with-connection pg
   (jdbc/transaction
     (create-stock_symbols)
     (create-stocks))))
