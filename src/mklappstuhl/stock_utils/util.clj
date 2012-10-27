(ns mklappstuhl.stock-utils.util
  (:require [clj-time.core :as time-core]
            [clj-time.format :as time-format]))

(def yfinance-date (time-format/formatter "yyyy-MM-dd"))

(defn parse-date [datestring]
  (time-format/parse yfinance-date datestring))
