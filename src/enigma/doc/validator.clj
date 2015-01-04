(ns enigma.doc.validator
  (:use [enigma.doc.validator.rule :only [-validate-rules]]))

(defmulti -validate-validator
  (fn [_ value] (type value)))

(defmethod -validate-validator clojure.lang.PersistentArrayMap
  [validator value]
  (validator value))

(defmethod -validate-validator clojure.lang.PersistentVector
  [validator value]
  (map validator value))

(defmulti validate
  (fn [rv _] (-> rv meta :type)))

(defmethod validate :rule
  [rule value]
  (-validate-rules rule value))

(defmethod validate :validator
  [validator value]
  (-validate-validator validator value))

(defmethod validate :default
  [_ value]
  {:fn-error "Fn is not a rule nor a validator."})

(def -default-settings
  {:type :validator
   :settings {:strict? true}})

(defn -validation-fn
  [map-settings map-value k]
  (let [-validation-fn (get map-settings k)
        value (get map-value k)
        result (validate -validation-fn value)]
    (when-not (nil? result)
      {k result})))

(defn -strict-validation
  [map-settings map-value]
  (let [key-settings (set (keys map-settings))
        key-value (keys map-value)]
    (vec (filter (fn [k]
                   (when (nil? (key-settings k)) k))
                 key-value))))

(defmacro defvalidator
  [name & args]
  (let [[docstring args] (if (-> args first string?)
                           [(first args) (next args)]
                           [nil args])
        [settings args] (if (-> args first map?)
                          [(update-in -default-settings
                                      [:settings]
                                      #(into % (first args))) (next args)]
                          [-default-settings args])]
    `(do
       (def ~name
         (with-meta
           (fn [value#]
             (if-not (map? value#)
               {:self-validation-error (str "Value is not a Map: " value#)}
               (let [map-rules# (hash-map ~@args)
                     ks# (keys map-rules#)
                     val-fn# (partial -validation-fn map-rules# value#)
                     result# (->> ks#
                                  (map #(val-fn# %))
                                  (filter #(not (nil? %)))
                                  (apply merge))
                     strict?# (-> ~settings :settings :strict? true?)]
                 (if-not strict?#
                   result#
                   (let [strict-result# (-strict-validation map-rules# value#)
                         final-result# (if (empty? strict-result#)
                                         result#
                                         (assoc result#
                                           :strict-error
                                           strict-result#))]
                     final-result#)))))
           (assoc ~settings :validator (hash-map ~@args))))
       (alter-meta! (var ~name)
                    assoc
                    :doc ~docstring))))
