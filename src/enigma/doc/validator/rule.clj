(ns enigma.doc.validator.rule
  (:use enigma.doc.validator.core))

(def -default-settings
  {:pass-nill? true
   :message (fn [settings data] (format "Error: %s" data))})

(defn- go?
  [data pass-nill?]
  (boolean (or (not (nil? data))
               (and (nil? data) (false? pass-nill?)))))

(defn- validate-with-rule
  [this data]
  (let [{:keys [body settings]} this
        {:keys [message pass-nill?]} settings]
    (when (go? data pass-nill?)
      (when-not (true? (body settings data))
        (message settings data)))))

(defn- construct-rule
  [this opts]
  (update-in this [:settings] into opts))

(defrecord Rule [body settings]
  ValidationFn
  (validate [this data]
    (validate-with-rule this data))
  (construct [this data]
    (construct-rule this data)))

(defn rule?
  [r]
  (instance? enigma.doc.validator.rule.Rule r))

(defmacro defrule
  [name & args]
  (let [[docstring args] (if (-> args first string?)
                           [(first args) (next args)]
                           [nil args])
        [settings args] (if (-> args first map?)
                          [(into -default-settings (first args)) (next args)]
                          [-default-settings args])
        [args body] [(first args) (next args)]]
    `(do
       (def ~name
         (let [body# (fn [~@args] ~@body)]
           (->Rule body# ~settings)))
       (alter-meta! (var ~name)
                    assoc
                    :doc ~docstring))))
