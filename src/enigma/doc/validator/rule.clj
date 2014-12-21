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

(defn- go-validate?
  [v pass-nill?]
  (or (not (nil? v))
      (and (nil? v) (false? pass-nill?))))

(defn- validate-rule
  [r v]
  (let [{:keys [message pass-nill?]} (-> r meta :settings)]
    (when (go-validate? v pass-nill?)
      (when-not (true? (r v))
        (message v)))))

(defn -validate-rules
  [r v]
  (let [{:keys [using message pass-nill?]} (-> r meta :settings)
        rules (into using [r])]
    (->> rules
         (map #(validate-rule % v))
         (filter #(not (nil? %)))
         first)))

(defn construct-rule
  [r opts]
  (let [m (meta r)
        new-meta (update-in m
                            [:settings]
                            #(into % opts))]
    (-> r
        (partial (:settings new-meta))
        (with-meta new-meta))))
