(ns mklappstuhl.stock-utils.simulate
  (:require [clj-time.core :as time]))

(defn buy [etf amount date]
  {:etf etf :amount amount :date date})

(defn fund [name & trades]
  {name trades})

(defn sampletrade []
  (buy "AAPL" 100000 (time/date-time 2012 1 1)))

(defn samplefund []
  (fund :compinvest1
        (buy "AAPL" 100000 (time/date-time 2011 1 1))
        (buy "IBM"  400000 (time/date-time 2011 1 1))
        (buy "GOOG" 200000 (time/date-time 2011 1 1))
        (buy "MSFT" 300000 (time/date-time 2011 1 1))))
