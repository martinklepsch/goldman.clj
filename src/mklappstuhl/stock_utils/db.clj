(ns mklappstuhl.stock-utils.db
  (:require [korma.db :as kdb]
            [korma.core :as k]
            [clojure.java.jdbc :as sql]))


(def db (kdb/postgres {:db "goldman"
                       :user "goldman"
                       :password ""}))

(defn persist-day [sym day-data]
  (sql/insert! db :stock day-data))

(defn drop-schema []
  (sql/with-connection db
    (sql/transaction
      (try
        ((sql/drop-table :migrations)
         (sql/drop-table :symbols)
         (sql/drop-table :market-days))
        (catch Exception e (.getNextException e))))))

(defn create-symbols []
  (try
    (sql/create-table
      :symbols
      [:id :serial "PRIMARY KEY"]
      [:name "varchar(15)" "UNIQUE"]  ;; longest stock-name is 5 characters long
      [:full_name "varchar(255)"]
      [:sector "varchar(255)"]
      [:industry "varchar(255)"])
    (catch Exception e (.getNextException e))))


(defn create-market-days []
  (try
    (sql/create-table
      :market-days
      [:id :serial "PRIMARY KEY"]
      [:date :date "UNIQUE NOT NULL" "DEFAULT CURRENT_DATE"] ;d2
      ;; TODO: what do we want in the db? ask/bid/volume...
      [:stock_symbol_id :serial "references symbols (id)"] ;; foreign key ;s
      [:high :integer "NOT NULL"] ;h
      [:low :integer "NOT NULL"] ;g
      [:open :integer "NOT NULL"] ;o
      [:close :integer "NOT NULL"] ; previous day - p
      [:volume :integer "NOT NULL"] ;v
      [:ask :integer "NOT NULL"] ;a
      [:bid :integer "NOT NULL"]) ;b
    (catch Exception e (.getNextException e))))

; this structure can be used to alter the current db schema
; (defn add-whatever []
;     (sql/do-commands "ALTER TABLE symbols ADD COLUMN whatever VARCHAR"))


; I basically took this from the link below. It's somehow a super lightweight
; migration system. Let's see how long this works. I didn't understand
; everything of this to be honest.
; https://github.com/technomancy/syme/blob/master/src/syme/db.clj#L99-L125
(defn run-and-record [migration]
  (println "Running migration:" (:name (meta migration)))
  (migration)
  (try
    (sql/insert-values "migrations" [:name :created_at]
                       [(str (:name (meta migration)))
                        (java.sql.Timestamp. (System/currentTimeMillis))])
    (catch Exception e (.getNextException e))))

(defn migrate [& migrations]
  (sql/with-connection db
    (try (sql/create-table "migrations"
                          [:name :varchar "NOT NULL"]
                          [:created_at :timestamp "NOT NULL"  "DEFAULT CURRENT_TIMESTAMP"])
         (catch Exception e (.getNextException e)))
    (sql/transaction
      (let [has-run? (sql/with-query-results run ["SELECT name FROM migrations"]
                       (set (map :name run)))]
        (doseq [m migrations 
                :when (not (has-run? (str (:name (meta m)))))]
          (run-and-record m))))))

(defn migrate-all []
  "run all migrations"
  (migrate #'create-symbols
           #'create-market-days))
