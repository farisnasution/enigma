(ns enigma.doc.validator.rule)

(def -default-settings
  {:type :rule
   :settings {:message (fn [v] (if v
                                (str "Error! Value is: " v)
                                "Error! Value is nil"))
              :pass-nill? true
              :using []}})

(defmacro defrule
  [name & args]
  (let [[docstring args] (if (-> args first string?)
                           [(first args) (next args)]
                           [nil args])
        [settings args] (if (-> args first map?)
                          [(update-in -default-settings
                                      [:settings]
                                      #(into % (first args))) (next args)]
                          [-default-settings args])
        [args body] [(first args) (next args)]]
    `(do
       (def ~name
         (with-meta
           (fn [~@args] ~@body)
           ~settings))
       (alter-meta! (var ~name)
                    assoc
                    :doc ~docstring))))
