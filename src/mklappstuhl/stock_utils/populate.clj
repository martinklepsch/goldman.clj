(ns mklappstuhl.stock-utils.populate
  (:require [mklappstuhl.stock-utils.persistence :as pers]
            [mklappstuhl.stock-utils.util :as util]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]
            [clojure.string :as string]
            [taoensso.timbre :as log]
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
                  (k/where {:stock_name (:name stock)})
                  (k/order :trading_date :desc)
                  (k/fields :trading_date)
                  (k/limit 1))))
      "2000-01-01"))

(defn sync-trading-data [stocks]
  "load Yahoo! Finance data for given stock and save it to database"
  (let [today (util/unparse-date (time/now))
        last-sync (str (last-synced-day (first stocks)))
        ticker (map #(keyword (:name %)) stocks)
        ; there is probably a limit of the stocks you can request at once
        data (yfinance/fetch-historical-data last-sync today ticker)]
    (map handle-yfincance-response (map #(vector % (str (:name %))) data) stocks)))

; (def up-to-date? [stock]
;   )
(sync-trading-data  (k/select pers/stocks (k/limit 2)))
; (map #(keyword (:name %)) (k/select pers/stocks (k/limit 3)))
; (yfinance/fetch-historical-data "2013-01-01" "2013-01-10" (map #(keyword (:name %)) (k/select pers/stocks (k/limit 3)))))


(defn handle-yfincance-response [stock data]
  "takes a response from yfinance and does things to it
   this should probably go into a yfinance adapter"
  (let [ticker (str (:name stock))]
    (cond
      (= data 404)
      (do
        (log/warn (name ticker) "- 404")
        (:name stock))


      (seq data)
      (do
        (log/info (name ticker) "- Successfully downloaded trading data"); from" last-sync "to" today)
        (pers/persist-days stock
                           (map #(update-in % [:trading_date] util/parse-date) data)))

      (empty? data)
      (log/info (name ticker) "- Empty response")

      :else
      (log/error (name ticker) "- Wierd stuff happening"))))


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
    (filter keyword? (map sync-trading-data stocks))))

; (populate-days)
; (sync-trading-data (first (k/select pers/stocks (k/where {:name "ACGX"}))))
; (keyword "tesT")
; (name (keyword (:name (first (k/select pers/stocks (k/limit 1))))))
; (populate-stocks "./resources/short.tsv" \tab [:name :full_name] [:name :full_name])
