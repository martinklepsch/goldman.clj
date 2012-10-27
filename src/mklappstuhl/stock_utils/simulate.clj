(ns mklappstuhl.stock-utils.simulate)

(defn buy [amount etf date]
  (hash-map :etf etf :amount amount :date date))

(defn fund [name & trades]
  (hash-map name
            trades))
