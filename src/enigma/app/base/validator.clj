(ns enigma.app.base.validator
  (:require [enigma.doc.field :as f])
  (:use [enigma.doc.validator :only [defvalidator]]))

(defvalidator base-validator
  :_id f/oid-field
  :date-created f/date-field)
