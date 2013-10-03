(ns mklappstuhl.stock-utils.populate
  (:require [mklappstuhl.stock-utils.persistence :as pers]
            [mklappstuhl.stock-utils.util :as util]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]
            [clojure.string :as string]
            [clojure.tools.logging :as log]
            [korma.core :as k]
            [korma.db :as kdb]
            [in.freegeek.yfinance :as yfinance]
            [clj-time.core :as time]))

;; csv can be downloaded at http://www.nasdaq.com/screening/companies-by-industry.aspx?exchange=NASDAQ&render=download
(defn populate-stocks [csv-name seperator header header-stripped]
  (with-open [csv-file (io/reader csv-name)]
    (let [parsed-csv (map (comp #(select-keys % header-stripped)
                                (partial zipmap header))
                          (doall (csv/read-csv csv-file :separator seperator)))]
       (map
         #(if
           (empty? (k/select pers/stocks (k/where { :name (:name %) })))
           (k/insert pers/stocks (k/values %))
           (log/info (:name %) "already in database"))
         parsed-csv))))


(defn last-synced-day [stock]
  (or (:trading_date
       (first
        (k/select pers/days
                  (k/where {:stock_name (name stock)})
                  (k/order :trading_date :desc)
                  (k/fields :trading_date)
                  (k/limit 1))))
      "2000-01-01"))

(defn sync-trading-data [stock]
  "load Yahoo! Finance data for given stock and save it to database"
  (let [today (util/unparse-date (time/now))
        last-sync (str (last-synced-day stock))
        data (stock (yfinance/fetch-historical-data last-sync today [stock]))]
    (if (not= data 404)
      ((log/info (name stock) " - fetching trading data from" last-sync "to" today)
      (pers/persist-days stock
                      (map #(update-in % [:trading_date] util/parse-date)
                           data)))
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
  (let [stocks (k/select pers/stocks)]
    (filter keyword? (map (comp sync-trading-data :name) stocks))))

;;(populate-stocks "./resources/short.tsv" \tab [:name :full_name] [:name :full_name])
;;(populate-days)
