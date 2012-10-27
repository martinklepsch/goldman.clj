(ns mklappstuhl.stock-utils.util
  (:require [clj-time.core :refer :all]
            [clj-time.format :refer :all]))

(def yfinance-date (formatter "yyyy-MM-dd"))

(defn parse-date [datestring]
  (parse yfinance-date datestring))
