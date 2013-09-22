(ns mklappstuhl.stock-utils.data
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [mklappstuhl.stock-utils.util :as util]
            [mklappstuhl.stock-utils.db :as db]
            [korma.core :as k]
            [in.freegeek.yfinance :as yfinance]
            [clj-time.core :as time]
            [clojure.data.csv :as csv]))

; (def sample (first (db/stock-sample)))

(defn parse-day [[x & xs]]
  "takes a line from Yahoo! Finance CSV data and returns
  a map where the keys are the column names"
  (zipmap [:trading_date :open :high :low :close :volume :adjusted_close]
          (cons (util/parse-date x)
                (map read-string xs))))

(defn parse-response [resp]
  "takes the response from a yfinance query and parses it to a list"
  (let [records (csv/read-csv resp)]
    (map parse-day (rest records))))

(defn last-synced-day [stock]
  (or (:trading_date
       (first
        (k/select db/days
                  (k/where {:stock_id 1})
                  (k/order :trading_date :desc)
                  (k/fields :trading_date)
                  (k/limit 1))))
      (time/date-time 2000 01 01)))

(defn sync-trading-data [stock]
  "load Yahoo! Finance data for given stock and save it to database"
  (let [{:keys [id sym]} stock
        today (util/unparse-date (time/now))
        last-sync (util/unparse-date (last-synced-day stock))
        data (get (yfinance/fetch-historical-data last-sync today [sym]) sym)]

    (map (partial db/persist-day id) (parse-response data))))

(defn load-trading-data [trade]
  ; Rewrite this function so that it downloads data
  ; in case of insufficient existing data
  ; - Date ranges need to be compared (last and second line in csv)
  ())
