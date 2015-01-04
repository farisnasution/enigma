(ns enigma.doc.mapper)

(def -default-settings {:type :mapper
                        :settings {}})

(defn- process?
  [map-settings map-value k]
  (let [s (get map-settings k)
        result (boolean (or (contains? map-value k)
                            (and (not (contains? map-value k))
                                 (true? s))))]
    result))

(defn- go-process
  [map-fn map-value k]
  (let [f (get map-fn k)
        v (get map-value k)
        result {k (f map-value v)}]
    result))

(defmacro defmapper
  [name & args]
  (let [[docstring args] (if (-> args first string?)
                           [(first args) (next args)]
                           [nil args])
        [settings args] (if (-> args first map?)
                          [(assoc -default-settings :settings (first args))
                           (next args)]
                          [-default-settings args])]
    `(do
       (def ~name
         (with-meta
           (fn [value#]
             (let [map-mapper# (hash-map ~@args)
                   process?# (partial process? ~settings value#)
                   go-process# (partial go-process map-mapper# value#)
                   ks# (keys map-mapper#)
                   result# (->> ks#
                                (filter #(process?# %))
                                (map #(go-process# %))
                                (apply merge))]
               result#))
           (assoc ~settings :mapper (hash-map ~@args))))
       (alter-meta! (var ~name)
                    assoc
                    :doc ~docstring))))
