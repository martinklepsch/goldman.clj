(ns mklappstuhl.stock-utils.metrics
  (:require [mklappstuhl.stock-utils.populate :as populate]
            [incanter.stats :as stats]))

(defn daily-returns [trade]
  "takes a trade and returns hash map of relative
  daily returns like {date return}"
  (let [adj-closes (map :adj-close (populate/load-trading-data trade))
        days       (map :date      (populate/load-trading-data trade))]
  (zipmap
    days
    (map dec (map / adj-closes (rest adj-closes))))))

(defn mean-daily-returns [trade]
  "takes symbol a trade and returns average of daily returns"
  (/ (reduce + (vals (daily-returns trade)))
     (count (daily-returns trade))))

(defn absolute-returns [trade]
  "takes a trade and returns the absolute returns in percentages"
  (let [first-day (:adj-close (first (populate/load-trading-data trade)))
        last-day  (:adj-close (last (populate/load-trading-data trade)))]
  (- (/ last-day first-day) 1)))

(defn sharpe-ratio [trade]
  "takes a trade and returns sharpe ratio"
  (let [k (Math/sqrt 250)]
    (* k (/ (mean-daily-returns trade)
            (stats/sd (vals (daily-returns trade)))))))
