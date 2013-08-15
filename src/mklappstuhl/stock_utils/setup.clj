(ns mklappstuhl.stock_utils.setup
  (:require [mklappstuhl.stock_utils.persistence :as persistence]
            [java.jdbc :as jdbc])


(defn create-stock_symbols
  "creates the table stocks in the database"
  []
  (jdbc/create-table
   :stock_symbol
   [:id :integer "PRIMARY KEY" "SERIAL"]
   [:name "varchar(7)"]  ;; longest stock-name is 5 characters long
   [:full_name "varchar(255)"]
   [:launch_date :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   ))

(defn create-stocks
  "creates the table stocks in the database"
  []
  (jdbc/create-table
   :stock
   [:id :integer "PRIMARY KEY" "SERIAL"]
   [:trading_date :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   ;; TODO: what do we want in the db? ask/bid/volume...
   [:stock_symbol_id :serial "references stock_symbol (id)"] ;; foreign key
   ))

(defn setup!
  "creates the necessary tables (stock,stock_symbol)"
  []
 (jdbc/with-connection
   persistence/pg
   (jdbc/transaction
     (create-stock_symbols)
     (create-stocks))))
