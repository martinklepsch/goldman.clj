(ns mklappstuhl.stock-utils.analyze
  (:require [clojure.java.io :as io]
            [mklappstuhl.stock-utils.util :as util]
            [in.freegeek.yfinance :as yfinance]
            [csvlib]))


; reference funtion call to get financial data for listed EFTs
; Returns hash-map {"AAPL" "csv stuff from yfinance" "IBM" "data"}
;(fetch-historical-data "2009-01-01" "2009-01-31" ["AAPL" "IBM" "MSFT" "GOOG"])

(def companies ["AAPL" "GOOG" "IBM" "CCL" "ADBE" "AMZN" "CSCO" "EBAY" "INTC"])

(defn save-trading-data [etfs]
  (let [data (yfinance/fetch-historical-data "2010-12-01" "2011-12-31" etfs)]
    (doseq [[etf csv] data]
      (with-open [wrtr (io/writer (str "data/" etf ".csv"))]
        (.write wrtr csv)))))

(defn parse-line [line]
  (zipmap '(:date :open :high :low :close :volume :adj-close)
           (cons (util/parse-date (first (vals line)))
                 (map read-string (rest (vals line))))))

(defn parse-file [etf]
  (let [records (csvlib/read-csv (str "data/" etf ".csv") :headers? true)]
    (map parse-line records)))

(defn load-trading-data [etf]
  (parse-file etf))
