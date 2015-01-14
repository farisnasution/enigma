(ns enigma.doc.validator.field
  (:require [enigma.doc.validator.rule :as dvr])
  (:use enigma.doc.validator.core))

(defn- validate-with-rules
  [{:keys [rules order]} data]
  (->> order
       (map (fn [o] (validate (get rules o) data)))
       (filter #(not (nil? %)))
       first))

(defn- construct-field
  [{:keys [rules body] :as this} opts]
  (let [new-rules (body rules opts)]
    (update-in this [:rules] into new-rules)))

(defrecord Field [rules order body]
  ValidationFn
  (validate [this data]
    (validate-with-rules this data))
  (construct [this data]
    (construct-field this data)))

(defmacro deffield
  [name & args]
  (let [[docstring args] (if (-> args first string?)
                           [(first args) (next args)]
                           [nil args])
        separator (juxt (fn [data]
                          (take-while #(not (vector? %)) data))
                        (fn [data]
                          (drop-while #(not (vector? %)) data)))
        [rules body-fn] (separator args)
        [args body] [(first body-fn) (next body-fn)]]
    `(do
       (def ~name
         (let [rules# (hash-map ~@rules)
               order# (take-nth 2 (list ~@rules))
               body# (fn [~@args] ~@body)]
           (->Field rules# order# body#)))
       (alter-meta! (var ~name)
                    assoc
                    :doc ~docstring))))
