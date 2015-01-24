(ns enigma.app.base.mapper
  (:require [enigma.util :as util])
  (:use [enigma.doc.mapper :only [defmapper]]))

(defn -str->oid
  [_ data]
  (if (nil? data)
    (util/->oid)
    (util/->oid data)))

(defn -oid->str
  [_ data]
  (str data))

(defn -auto-spawn-now
  [_ data]
  (util/now))

(defmapper base-jsonable-mapper
  :_id -oid->str)

(defmapper base-saveable-mapper
  {:_id true}
  :_id -str->oid
  :date-created -auto-spawn-now)
