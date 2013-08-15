(ns mklappstuhl.stock_utils.persistence
  (:require [korma.db :as db]
            [korma.core :as k])

(def c (db/postgres {:db 'goldman'
                     :user 'goldman'
                     :password 'goldman'}))

(k/defentity symbol
  (table-fields :street :city :zip))
