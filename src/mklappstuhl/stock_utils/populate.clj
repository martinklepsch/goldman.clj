(ns mklappstuhl.stock-utils.populate
  (:require [mklappstuhl.stock-utils.db :as db]
            [mklappstuhl.stock-utils.util :as util]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]
            [clojure.java.jdbc :as jdbc]
            [clojure.string :as string]
            [korma.core :as k]
            [in.freegeek.yfinance :as yfinance]
            [clj-time.core :as time]
            [clojure.data.csv :as csv]))

;; csv can be downloaded at http://www.nasdaq.com/screening/companies-by-industry.aspx?exchange=NASDAQ&render=download
(defn populate-stocks [csv-name]
 (with-open [csv-file (io/reader csv-name)]
  (let [header [:name :full_name :lastSale :MarketCap :ADR_TSO :IPOyear :sector :industry :summary_quote]
        header-stripped [:name :full_name :sector :industry]
        parsed-csv (map (comp #(select-keys % header-stripped)
                              (partial zipmap header))
                        (csv/read-csv csv-file))]
    (apply (partial jdbc/insert! db/pg :stocks)
           (rest parsed-csv)))))

(defn last-synced-day [stock]
  (or (:trading_date
       (first
        (k/select db/days
                  (k/where {:stock_name (name stock)})
                  (k/order :trading_date :desc)
                  (k/fields :trading_date)
                  (k/limit 1))))
      (time/date-time 2000 01 01)))

(defn sync-trading-data [stock]
  "load Yahoo! Finance data for given stock and save it to database"
  (let [today (util/unparse-date (time/now))
        last-sync (util/unparse-date (last-synced-day stock))
        data (stock (yfinance/fetch-historical-data last-sync today [stock]))]
    (if (not= data 404)
      (db/persist-day stock
                      (map #(update-in % [:trading_date] util/parse-date)
                           data))
      stock)))

(def marijuana
  [:CANV :CBIS :EDXC :ERBB :FSPM :GRNH :GWPL
   :GWPRF :HEMP :HSCC :MDBX :MJNA :MWIP :NVLX
   :PHOT :RFMK :RIGH :TRTC :XCHC])

(defn load-trading-data [trade]
  ; Rewrite this function so that it downloads data
  ; in case of insufficient existing data
  ; - Date ranges need to be compared (last and second line in csv)
  ())

(defn populate-days []
  "populate the days table, returns a list of the symbols where yahoo returned 404"
  (let [stocks (k/select db/stocks)]
    (filter keyword? (map (comp sync-trading-data :name) stocks))))
