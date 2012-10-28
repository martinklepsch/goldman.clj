(ns mklappstuhl.stock-utils.simulate)

(defn buy [amount etf date]
  {:etf etf :amount amount :date date})

(defn fund [name & trades]
  {name trades})
