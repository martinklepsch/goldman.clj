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

(defn handle-yfincance-response [data]
  "takes a response from yfinance and does things to it
   this should probably go into a yfinance adapter"
  (let [stock (first (k/select pers/stocks (k/where {:name (key data)})))
        days (map #(update-in % [:trading_date] util/parse-date) (first (rest data)))
        ticker (str (:name stock))]
    (cond
      (= days 404)
      (do
        (log/warn (name ticker) "- 404")
        (:name stock))

      (seq days)
      (do
        (log/info (name ticker) "- downloaded" (count days) "days of trading data")
        (pers/persist-days stock days))

      (empty? days)
      (log/info (name ticker) "- Empty response")

      :else
      (log/error (name ticker) "- Wierd stuff happening"))))



(defn sync-trading-data [stocks]
  "load Yahoo! Finance data for given stock and save it to database"
  (let [today (util/unparse-date (time/now))
        last-sync (str (last-synced-day (first stocks)))
        ticker (map #(:name %) stocks)
        ; there is probably a limit of the stocks you can request at once
        data (yfinance/fetch-historical-data last-sync today ticker)]
    (map handle-yfincance-response data)))

(defn populate-days []
  "populate the days table, returns a list of the symbols where yahoo returned 404"
  (let [stocks (k/select pers/stocks)]
    (filter keyword? (map sync-trading-data stocks))))

; (populate-days)
; (sync-trading-data (first (k/select pers/stocks (k/where {:name "ACGX"}))))
; (keyword "tesT")
; (name (keyword (:name (first (k/select pers/stocks (k/limit 1))))))
; (populate-stocks "./resources/short.tsv" \tab [:name :full_name] [:name :full_name])
