(ns enigma.doc.validator
  (:use enigma.doc.validator.core))

(def -default-settings
  {:strict? true})

(defn -strict-validation
  [map-body map-data]
  (let [key-settings (set (keys map-body))
        key-value (keys map-data)]
    (vec (filter (fn [k]
                   (when (nil? (key-settings k)) k))
                 key-value))))

(defn- validation-fn
  [map-body map-data]
  (fn [k]
    (let [func (get map-body k)
          data (get map-data k)
          result (func data)]
      (when-not (nil? result)
        {k result}))))

(defn- validate-with-validator
  [{:keys [body settings]} data]
  (if-not (map? data)
    {:self-validation-error (format "Value is not a Map: %s" data)}
    (let [strict? (:strict? settings)
          ks (keys body)
          v-fn (validation-fn body data)
          result (->> ks
                      (map v-fn)
                      (filter #(not (nil? %)))
                      (apply merge))]
      (if-not (true? strict?)
        result
        (let [strict-result (-strict-validation body data)
              final-result (if (empty? strict-result)
                             result
                             (assoc result :strict-error strict-result))]
          final-result)))))

(defn- construct-validator
  [this opts]
  (assoc this :settings opts))

(defrecord Validator [body settings]
  ValidationFn
  (validate [this data]
    (validate-with-validator this data))
  (construct [this data]
    (construct-validator this data)))

(defn validator?
  [v]
  (instance? enigma.doc.validator.Validator v))

(defmacro defvalidator
  [name & args]
  (let [[docstring args] (if (-> args first string?)
                           [(first args) (next args)]
                           [nil args])
        [settings args] (if (-> args first map?)
                          [(first args) (next args)]
                          [-default-settings args])
        [parent args] (if (-> args first validator?)
                        [(first args) (next args)]
                        [{} args])]
    `(do
       (def ~name
         (let [parent-body# (:body ~parent)
               parent-settings# (:settings ~parent)
               body# (if-not (nil? parent-body#)
                       (into parent-body# (hash-map ~@args))
                       (hash-map ~@args))
               settings# (if-not (nil? parent-settings#)
                           (into parent-settings# ~settings)
                           ~settings)]
           (->Validator body# settings#)))
       (alter-meta (var ~name)
                   assoc
                   :doc ~docstring))))
