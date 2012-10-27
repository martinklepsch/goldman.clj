(ns mklappstuhl.stock-utils.metrics
  (:require [mklappstuhl.stock-utils.data :as data]))

(defn daily-returns [etf]
  (let [adj-closes (map :adj-close (data/load-trading-data etf))
        days       (map :date      (data/load-trading-data etf))]
    (zipmap
      days
      (map dec (map / adj-closes (rest adj-closes))))))

(defn mean-daily-returns [etf]
  (/ (reduce + (vals (daily-returns etf)))
     (count (daily-returns etf))))

(defn standard-deviation [samples]
  (let [n (count samples)
        mean (/ (reduce + samples) n)
        intermediate (map #(Math/pow (- %1 mean) 2) samples)]
    (Math/sqrt
      (/ (reduce + intermediate) n))))

(defn sharpe-ratio [etf]
  (let [k (Math/sqrt 250)]
    (* k (/ (mean-daily-returns etf)
            (standard-deviation (vals (daily-returns etf)))))))
