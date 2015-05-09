(ns enigma.doc.field
  (:require [enigma.doc.rule :as r]
            [enigma.doc.validator.field :as vf])
  (:use enigma.doc.validator.core))

(defn- extract-required
  [rule value]
  (if-not (nil? value)
    (not value)
    (not (-> rule :settings :pass-nill?))))

(defmacro extract-settings
  [rule value ks]
  `(if-not (nil? ~value)
     ~value
     (-> ~rule :settings ~@ks)))

(vf/deffield string-field
  :required r/required
  :str-only r/str-only
  :regex r/regex
  :min-length r/min-length
  :max-length r/max-length
  [{:keys [required max-length min-length regex]}
   {:keys [required? max-length? min-length? regex-config]}]
  (let [required? (extract-required required required?)
        min-length? (extract-settings min-length min-length? [:threshold])
        max-length? (extract-settings max-length max-length? [:threshold])
        regex-config (extract-settings regex regex-config [:regex-config])]
   {:required (construct required {:pass-nill? required?})
    :min-length (construct min-length {:threshold min-length?})
    :max-length (construct max-length {:threshold max-length?})
    :regex (construct regex {:regex-config regex-config})}))

(vf/deffield int-field
  :required r/required
  :int-only r/int-only
  :min-value r/min-value
  :max-value r/max-value
  [{:keys [required min-value max-value]}
   {:keys [required? max-value? min-value?]}]
  (let [required? (extract-required required required?)
        min-value? (extract-settings min-value min-value? [:threshold])
        max-value? (extract-settings max-value max-value? [:threshold])]
    {:required (construct required {:pass-nill? required?})
     :min-value (construct min-value {:threshold min-value?})
     :max-value (construct max-value {:threshold max-value?})}))

(vf/deffield float-field
  :required r/required
  :float-only r/float-only
  :min-value r/min-value
  :max-value r/max-value
  [{:keys [required min-value max-value]}
   {:keys [required? max-value? min-value?]}]
  (let [required? (extract-required required required?)
        min-value? (extract-settings min-value min-value? [:threshold])
        max-value? (extract-settings max-value max-value? [:threshold])]
    {:required (construct required {:pass-nill? required?})
     :min-value (construct min-value {:threshold min-value?})
     :max-value (construct max-value {:threshold max-value?})}))

(vf/deffield vec-field
  :required r/required
  :vec-only r/vec-only
  :cannot-empty r/cannot-empty
  [{:keys [required cannot-empty]}
   {:keys [required? can-empty?]}]
  (let [required? (extract-required required required?)
        can-empty? (extract-settings cannot-empty can-empty? [:can?])]
    {:required (construct required {:pass-nill? required?})
     :cannot-empty (construct cannot-empty can-empty?)}))

(vf/deffield set-field
  :required r/required
  :set-only r/set-only
  :cannot-empty r/cannot-empty
  [{:keys [required cannot-empty]}
   {:keys [required? can-empty?]}]
  (let [required? (extract-required required required?)
        can-empty? (extract-settings cannot-empty can-empty? [:can?])]
    {:required (construct required {:pass-nill? required?})
     :cannot-empty (construct cannot-empty can-empty?)}))

(vf/deffield map-field
  :required r/required
  :map-only r/map-only
  :cannot-empty r/cannot-empty
  [{:keys [required cannot-empty]}
   {:keys [required? can-empty?]}]
  (let [required? (extract-required required required?)
        can-empty? (extract-settings cannot-empty can-empty? [:can?])]
    {:required (construct required {:pass-nill? required?})
     :cannot-empty (construct cannot-empty can-empty?)}))

(vf/deffield email-field
  :required r/required
  :str-only r/str-only
  :email-only r/email-only
  [{:keys [required]}
   {:keys [required?]}]
  (let [required? (extract-required required required?)]
    {:required (construct required {:pass-nill? required?})}))

(vf/deffield slug-field
  :required r/required
  :str-only r/str-only
  :slug-only r/slug-only
  [{:keys [required]}
   {:keys [required?]}]
  (let [required? (extract-required required required?)]
    {:required (construct required {:pass-nill? required?})}))

(vf/deffield url-field
  :required r/required
  :str-only r/str-only
  :url-only r/url-only
  [{:keys [required]}
   {:keys [required?]}]
  (let [required? (extract-required required required?)]
    {:required (construct required {:pass-nill? required?})}))

(vf/deffield date-field
  :required r/required
  :date-only r/date-only
  [{:keys [required]}
   {:keys [required?]}]
  (let [required? (extract-required required required?)]
    {:required (construct required {:pass-nill? required?})}))

(vf/deffield bool-field
  :required r/required
  :bool-only r/bool-only
  [{:keys [required]}
   {:keys [required?]}]
  (let [required? (extract-required required required?)]
    {:required (construct required {:pass-nill? required?})}))
