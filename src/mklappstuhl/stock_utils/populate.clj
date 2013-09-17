(ns mklappstuhl.stock-utils.populate
  (:require [mklappstuhl.stock-utils.db :as db]
            [clojure.java.io :as io]
            [in.freegeek.yfinance :as yfinance]
            [clojure.data.csv :as csv]
            [clojure.java.jdbc :as jdbc]))

;; csv can be downloaded at http://www.nasdaq.com/screening/companies-by-industry.aspx?exchange=NASDAQ&render=download
(defn populate-symbols [csv-name]
 (with-open [csv-file (io/reader csv-name)]
  (let [header [:name  :full_name :lastSale :MarketCap :ADR_TSO :IPOyear :sector :industry :summary_quote]
        header-stripped [:name :full_name :sector :industry]
        parsed-csv (map (comp #(select-keys % header-stripped)
                              (partial zipmap header))
                        (csv/read-csv csv-file))]
    (apply (partial jdbc/insert! db/db :symbols)
           (rest parsed-csv)))))




