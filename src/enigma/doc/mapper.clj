(ns enigma.doc.mapper)

(defn- process?
  [settings data]
  (fn [k]
    (let [key-contained? (contains? data k)]
      (boolean (or key-contained?
                   (and (not key-contained?)
                        (true? (get settings k))))))))

(defn- go-process-inner
  [body data]
  (fn [k]
    (let [from-body (get body k)
          from-data (get data k)]
      {k (from-body data from-data)})))

(defn- go-process
  [{:keys [body settings]} data]
  (when (map? data)
    (let [ks (keys body)
          process?-fn (process? settings data)
          go-process-inner-fn (go-process-inner body data)]
      (->> ks
           (filter process?-fn)
           (map go-process-inner-fn)
           (apply merge data)))))

(defprotocol MappingFn
  (process [this data]))

(defrecord Mapper [body settings]
  MappingFn
  (process [this data]
    (go-process this data)))

(defn mapper?
  [m]
  (instance? enigma.doc.mapper.Mapper m))

(defmacro defmapper
  [name & args]
  (let [[docstring args] (if (-> args first string?)
                           [(first args) (next args)]
                           [nil args])
        [settings args] (if (-> args first map?)
                          [(first args) (next args)]
                          [{} args])]
    `(do
       (def ~name
         (let [args# (list ~@args)
               [parent# args#] (if (-> args# first mapper?)
                                 [(first args#) (next args#)]
                                 [{} args#])
               {parent-body# :body parent-settings# :settings} parent#
               body# (if-not (nil? parent-body#)
                       (into parent-body# (apply hash-map args#))
                       (apply hash-map args#))
               settings# (if-not (nil? parent-settings#)
                           (into parent-settings# ~settings)
                           ~settings)]
           (->Mapper body# settings#)))
       (alter-meta! (var ~name)
                    assoc
                    :doc ~docstring))))
