(ns enigma.resource.util)

(defn body-type
  [request]
  (let [body (:body request)]
    (cond
     (map? body) :single
     (vector? body) :list)))
