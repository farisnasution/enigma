(ns enigma.doc.mapper)

;; (def -default-settings {:type :mapper
;;                         :settings {}})

;; (defn- process?
;;   [map-settings map-value k]
;;   (let [s (get map-settings k)
;;         result (boolean (or (contains? map-value k)
;;                             (and (not (contains? map-value k))
;;                                  (true? s))))]
;;     result))

;; (defn- go-process
;;   [map-fn map-value k]
;;   (let [f (get map-fn k)
;;         v (get map-value k)
;;         result {k (f map-value v)}]
;;     result))

;; (defmacro defmapper
;;   [name & args]
;;   (let [[docstring args] (if (-> args first string?)
;;                            [(first args) (next args)]
;;                            [nil args])
;;         [settings args] (if (-> args first map?)
;;                           [(assoc -default-settings :settings (first args))
;;                            (next args)]
;;                           [-default-settings args])]
;;     `(do
;;        (def ~name
;;          (with-meta
;;            (fn [value#]
;;              (let [map-mapper# (hash-map ~@args)
;;                    process?# (partial process? ~settings value#)
;;                    go-process# (partial go-process map-mapper# value#)
;;                    ks# (keys map-mapper#)
;;                    result# (->> ks#
;;                                 (filter #(process?# %))
;;                                 (map #(go-process# %))
;;                                 (apply merge))]
;;                result#))
;;            (assoc ~settings :mapper (hash-map ~@args))))
;;        (alter-meta! (var ~name)
;;                     assoc
;;                     :doc ~docstring))))

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
  (let [ks (keys body)
        process?-fn (process? settings data)
        go-process-inner-fn (go-process-inner body data)]
    (->> ks
         (filter process?-fn)
         (map go-process-inner-fn)
         (apply merge))))

(defprotocol MappingFn
  (process [this data]))

(defrecord Mapper [body settings]
  MappingFn
  (process [this data]
    (go-process this data)))

(defn mapper?
  [m]
  (instance? enigma.doc.mapper.Mapper m ))

(defmacro defmapper
  [name & args]
  (let [[docstring args] (if (-> args first string?)
                           [(first args) (next args)]
                           [nil args])
        [settings args] (if (-> args first map?)
                          [(first args) (next args)]
                          [{} args])
        [parent args] (if (-> args first mapper?)
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
                           ~settings)]))
       (alter-meta (var ~name)
                   assoc
                   :doc ~docstring))))
