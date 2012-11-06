(ns mklappstuhl.stock-utils.data
  (:require [clojure.java.io :as io]
            [mklappstuhl.stock-utils.util :as util]
            [in.freegeek.yfinance :as yfinance]
            [clj-time.core :as time]
            [csvlib]))


(def companies ["AAPL" "GOOG" "IBM" "CCL" "ADBE" "AMZN" "CSCO" "EBAY" "INTC"])

(defn save-trading-data [etfs]
  "load Yahoo! Finance data for given symbol and save it to data/ directory"
  (let [data (yfinance/fetch-historical-data "2010-12-01" "2011-12-31" etfs)]
    (doseq [[etf csv] data]
      (with-open [wrtr (io/writer (str "data/" etf ".csv"))]
        (.write wrtr csv)))))

(defn parse-line [line]
  "takes a line from Yahoo! Finance CSV data and returns
  a map where the keys are the column names"
  (zipmap '(:date :open :high :low :close :volume :adj-close)
           (cons (util/parse-date (first (vals line)))
                 (map read-string (rest (vals line))))))

; (defn after-trade? [day trade]
;   (after? (:date day) (:date trade)))

(defn parse-file [trade]
  "read CSV file and parse each line with parse-line"
  (let [records (csvlib/read-csv (str "data/" (:etf trade) ".csv") :headers? true)]
    (sort-by :date
      (filter (fn [day] (time/after? (:date day) (:date trade)))
        (map parse-line records)))))

(defn load-trading-data [trade]
  ; Rewrite this function so that it downloads data
  ; in case of insufficient existing data
  ; - Date ranges need to be compared (last and second line in csv)
  (parse-file trade))
