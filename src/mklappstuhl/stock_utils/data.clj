(ns mklappstuhl.stock-utils.data
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.pprint :as pp]
            [mklappstuhl.stock-utils.util :as util]
            [mklappstuhl.stock-utils.db :as db]
            [in.freegeek.yfinance :as yfinance]
            [clj-time.core :as time]
            [clojure.data.csv :as csv]))


(defn parse-day [line]
  "takes a line from Yahoo! Finance CSV data and returns
  a map where the keys are the column names"
  (println line)
  (zipmap '(:trading_date :open :high :low :close :volume :adjusted_close)
           (cons (util/parse-date (first line))
                 (map read-string (rest line)))))

(defn parse-response [resp]
  "takes the response from a yfinance query and parses it to a li"
  ; (let [records (csv/read-csv resp)]
    (println resp)
    (map parse-day (rest records)))

(defn sync-trading-data [stock]
  "load Yahoo! Finance data for given symbol and save it to data/ directory"
  (let [id (:id stock)
        sym (:name stock)
        data (yfinance/fetch-historical-data "2011-12-09" "2011-12-15" [sym])]
    (parse-response data)))

(defn load-trading-data [trade]
  ; Rewrite this function so that it downloads data
  ; in case of insufficient existing data
  ; - Date ranges need to be compared (last and second line in csv)
  ())
