(ns mklappstuhl.stock-utils.metrics
  (:require [mklappstuhl.stock-utils.populate :as populate]
            [mklappstuhl.stock-utils.persistence :as pers]
            [clj-time.coerce :as coerce]
            [korma.core :as k]
            [incanter.stats :as stats]))

(defn daily-returns [trade]
  "takes a trade and returns hash map of relative
  daily returns like {date return}"
  (let [adj-closes (map :adj-close (populate/sync-trading-data trade))
        days       (map :date      (populate/sync-trading-data trade))]
  (zipmap
    days
    (map dec (map / adj-closes (rest adj-closes))))))

(defn mean-daily-returns [trade]
  "takes symbol a trade and returns average of daily returns"
  (/ (reduce + (vals (daily-returns trade)))
     (count (daily-returns trade))))

(defn absolute-returns [trade]
  "takes a trade and returns the absolute returns in percentages"
  (let [first-day (:adj-close (first (populate/sync-trading-data trade)))
        last-day  (:adj-close (last (populate/sync-trading-data trade)))]
  (- (/ last-day first-day) 1)))

(defn sharpe-ratio [trade]
  "takes a trade and returns sharpe ratio"
  (let [k (Math/sqrt 250)]
    (* k (/ (mean-daily-returns trade)
            (stats/sd (vals (daily-returns trade)))))))

(defn stock-data->nvd3 [days]
  "takes a days-map and returns and equivalent of a nvd3 datastructure
  [{:stock-name 'AABB', :bid 0.5, :trading-date '2013-...' ... }, {..}, ...] ->
  [{:key 'AABB-bid', :values [['2013-..' 0.5]]}]"
  (let [stock-name (:stock_name (first days))
        x-key :trading_date
        y-keys [:adjusted_close :low :high :ask :bid]]
    (map (fn [y-key] {:key (str stock-name "-" (name y-key))
                      :values (map (juxt (comp coerce/to-long x-key) y-key) days)})
         y-keys)))

(defn get-stock-data [stock]
  (k/select pers/days
            (k/where {:stock_name (name stock)})))
(defn get-stocks []
  (k/select pers/stocks))

; (get-stock-data :AABB)


