(ns enigma.doc.validator.rule
  (:require [enigma.doc.validator.core :as v]))

(v/defrule required
  {:message (fn [settings value] "Value cannot be nil.")
   :pass-nill? false}
  [_ value]
  (not (nil? value)))

(v/defrule str-only
  {:message (fn [_ value]
              (str "Value should be string: " value))}
  [_ value]
  (string? value))

(v/defrule min-length
  {:threshold 0
   :message (fn [{:keys [threshold]} value]
              (str "Value's length is below threshold. "
                   "Value: " value "."
                   "Threshold: " threshold "."))}
  [{:keys [threshold]} value]
  (> (count value) threshold))
