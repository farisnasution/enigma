(ns enigma.doc.rule
  (:use enigma.doc.validator.core)
  (:require [enigma.doc.validator.rule :as vr]))

(vr/defrule required
  {:message (fn [settings value] "Value cannot be nil.")
   :pass-nill? false}
  [_ value]
  (not (nil? value)))

(vr/defrule str-only
  {:message (fn [_ value]
              (str "Value should be string: " value))}
  [_ value]
  (string? value))

(vr/defrule min-length
  {:threshold -1
   :message (fn [{:keys [threshold]} value]
              (str "Value's length is below or equal to threshold. "
                   "Value: " value "."
                   "Threshold: " threshold "."))}
  [{:keys [threshold]} value]
  (or (= threshold -1) (> (count value) threshold)))

(vr/defrule max-length
  {:threshold -1
   :message (fn [{:keys [threshold]} value]
              (str "Value's length is above or equal to threshold. "
                   "Value: " value "."
                   "Threshold: " threshold "."))}
  [{:keys [threshold]} value]
  (or (= threshold -1) (< (count value) threshold)))

(vr/defrule int-only
  {:message (fn [_ value]
              (str "Value should be an int: " value))}
  [_ value]
  (integer? value))

(vr/defrule float-only
  {:message (fn [_ value]
              (str "Value should be a float: " value))}
  [_ value]
  (float? value))

(vr/defrule min-value
  {:threshold -1
   :message (fn [{:keys [threshold]} value]
              (str "Value is below or equal to threshold. "
                   "Value: " value "."
                   "Threshold: " threshold "."))}
  [{:keys [threshold]} value]
  (or (= threshold -1) (> value threshold)))

(vr/defrule max-value
  {:threshold -1
   :message (fn [{:keys [threshold]} value]
              (str "Value's length is above or equal to threshold. "
                   "Value: " value "."
                   "Threshold: " threshold "."))}
  [{:keys [threshold]} value]
  (or (= threshold -1) (< value threshold)))

(vr/defrule vec-only
  {:message (fn [_ value]
              (str "Value is not a vector: " value "."))}
  [_ value]
  (vector? value))

(vr/defrule map-only
  {:message (fn [_ value]
              (str "Value is not a map: " value "."))}
  [_ value]
  (map? value))

(vr/defrule set-only
  {:message (fn [_ value]
              (str "Value is not a set: " value "."))}
  [_ value]
  (set? value))

(vr/defrule cannot-empty
  {:can? false
   :message (fn [_ value]
              (str "Value is empty: " value "."))}
  [{:keys [can?]} value]
  (or (true? can?) (boolean (seq value))))

(vr/defrule pos-only
  {:message (fn [_ value]
              (str "Value is not positive: " value "."))}
  [_ value]
  (pos? value))

(vr/defrule neg-only
  {:message (fn [_ value]
              (str "Value is not negative: " value "."))}
  [_ value]
  (neg? value))

(vr/defrule date-only
  {:message (fn [_ value]
              (str "Value is not a date. "
                   "Value: " value ". "
                   "Type: " (type value)))}
  [_ value]
  (instance? java.util.Date value))

(vr/defrule regex
  {:regex-config #".+"
   :message (fn [{:keys [regex-config]} value]
              (str "Value didn't match with the regex given. "
                   "Value: " value ". "
                   "Regex: " regex-config "."))}
  [{:keys [regex-config]} value]
  (and (string? value) (boolean (re-matches regex-config value))))

(vr/defrule email-only
  {:message (fn [_ value]
              (str "Value is not email-compatible: " value "."))}
  [_ value]
  (let [regex-config #"[a-zA-Z0-9.+_-]+@[a-zA-Z0-9.+_-]+\.[a-zA-Z]{2,4}"]
    (-> regex
        (construct {:regex-config regex-config})
        (validate value)
        boolean)))

(vr/defrule bool-only
  {:message (fn [_ value]
              (str "Value is not a boolean: " value "."))}
  [_ value]
  (or (true? value) (false? value)))

(vr/defrule slug-only
  {:message (fn [_ value]
              (str "Value is not a slug-compatible: " value "."))}
  [_ value]
  (let [regex-config #"^[a-z0-9]+(?:-[a-z0-9]+)*$"]
    (-> regex
        (construct {:regex-config regex-config})
        (validate value)
        boolean)))

(vr/defrule oid-only
  {:message (fn [_ value]
              (str "Value is not an instance of ObjectId. "
                   "Value: " value ". "
                   "Type: " (if-not (nil? value)
                              (type value)
                              "nil") "."))}
  [_ value]
  (instance? org.bson.types.ObjectId (type value)))

(vr/defrule every
  {:message (fn [_ v] "Value doesn't satisfy the given :every-fn.")}
  [{:keys [every-fn]} value]
  (every? every-fn value))

(vr/defrule any
  {:message (fn [_ v] "Value doesn't satisfy the given :any-fn.")}
  [{:keys [any-fn]} value]
  (some any-fn value))
