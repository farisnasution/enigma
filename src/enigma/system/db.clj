(ns enigma.system.db
  (:require [monger.core :as mg]))

(defn store-db!
  [a]
  (fn [host port db-name instance]
    (swap! a (fn [current h p d i]
               (assoc current d {:host h
                                 :port p
                                 :db-name d
                                 :instance i})) host port db-name instance)))

(defn remove-db!
  [a]
  (fn [db-name]
    (swap! a dissoc db-name)))

(defn retrieve-db
  [a]
  (fn [db-name]
    (get @a db-name)))
