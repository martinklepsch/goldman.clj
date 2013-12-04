(ns mklappstuhl.stock-utils.util
  (:require [clj-time.core :as t]
            [clj-time.coerce :as coerce]
            [clj-time.format :as format]
            [clojure.data.csv :as csv]))

(def yfinance-date (format/formatter "yyyy-MM-dd"))

(defn parse-date [datestring]
  (coerce/to-sql-date (format/parse yfinance-date datestring)))

(defn unparse-date [date]
  (format/unparse yfinance-date date))

(defn full-directory-list
  "Given a directory, return the full pathnames for the files it contains"
  [dir]
  (sort (map #(.getCanonicalPath %) (.listFiles (clojure.java.io/file dir)))))
