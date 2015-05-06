(ns enigma.app.user.mapper
  (:require [enigma.app.base.mapper :as bm]
            [buddy.hashers.bcrypt :as hs])
  (:use [enigma.doc.mapper :only [defmapper]]))

(defmapper user->jsonable
  bm/base-jsonable-mapper
  :roles (fn [_ data] (vec data)))

(defmapper user->saveable
  {:roles true}
  bm/base-saveable-mapper
  :password (fn [_ data]
              (hs/make-password data)))

(defmapper user->updateable
  bm/base-saveable-mapper)
