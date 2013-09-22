(ns mklappstuhl.stock-utils.db
  (:require [korma.db :as kdb]
            [korma.core :as k]
            [clojure.pprint :as pp]
            [clojure.java.jdbc :as sql]))


(def pg (kdb/postgres {:db "goldman"
                       :user "goldman"
                       :password ""
                       :host "localhost"
                       :port "5432"}))

(k/defentity stocks
  (k/database pg))

(k/defentity days
  (k/database pg)
  (k/has-one stocks))

(k/defentity
 stocks
 (k/database pg)
 (k/has-many days)
 (k/transform
  (fn [m]
    (update-in m [:name] keyword))))

(defn stock-sample  []
  (k/select stocks
    (k/limit 1)))

(defn persist-day [stock-id day-data]
  (let [data (merge {:stock_id stock-id} day-data)]
    (k/insert days
      (k/values data))))

(defn drop-schema []
  (sql/with-connection pg
    (sql/transaction
       (try (sql/drop-table :migrations))
       (try (sql/drop-table :stocks))
       (try (sql/drop-table :days)))))

(defn create-stocks []
    (sql/create-table
      :stocks
      [:id :serial "PRIMARY KEY"]
      [:name "varchar(15)" "UNIQUE"]  ;; longest stock-name is 5 characters long
      [:full_name "varchar(255)"]
      [:sector "varchar(255)"]
      [:industry "varchar(255)"]))


(defn create-days []
    (sql/create-table
      :days
      [:id :serial "PRIMARY KEY"]
      [:trading_date :date "UNIQUE NOT NULL" "DEFAULT CURRENT_DATE"] ;d2
      ;; TODO: what do we want in the db? ask/bid/volume...
      [:stock_id :serial "references stocks (id)"] ;; foreign key ;s
      [:open "NUMERIC(16, 4)" "NOT NULL"] ;o
      [:high "NUMERIC(16, 4)" "NOT NULL"] ;h
      [:low "NUMERIC(16, 4)" "NOT NULL"] ;g
      [:close "NUMERIC(16, 4)" "NOT NULL"] ; previous day - p
      [:volume :integer "NOT NULL"] ;v
      [:adjusted_close "NUMERIC(16, 4)" "NOT NULL"] ;  ????
      [:ask "NUMERIC(16, 4)"] ;a
      [:bid "NUMERIC(16, 4)"])) ;b

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
  (sql/with-connection pg
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
  (migrate #'create-stocks
           #'create-days))
