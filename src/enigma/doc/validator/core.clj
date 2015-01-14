(ns enigma.doc.validator.core)

(defprotocol ValidationFn
  (validate [this data])
  (construct [this data]))
