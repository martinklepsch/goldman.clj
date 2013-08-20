(ns mklappstuhl.stock_utils.persistence
  (:require [korma.db :as db]
            [korma.core :as k]))

(def pg (db/postgres {:db "goldman"
                      :user "goldman"
                      :password ""}))

