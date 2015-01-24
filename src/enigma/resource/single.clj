(ns enigma.resource.single
  (:require [monger [collection :as mc]])
  (:use [enigma.util :only [->oid]]
        [enigma.doc.mapper :only [process]]
        [enigma.doc.validator.core :only [validate]]))

(defn- exists-fn
  [db-fn coll-name id]
  (fn [{:keys [request]}]
    (let [db-instance (db-fn request)
          oid (->oid id)
          entity (mc/find-map-by-id db-instance
                                    coll-name
                                    oid)
          result (if-not (nil? entity)
                   [true {:entity entity}]
                   [false {:entity []}])]
      result)))

(defn- get-entity-fn
  [jsonable-mapper]
  (fn [{:keys [entity]}]
    (process jsonable-mapper entity)))

(defn- put-entity-fn
  [db-fn coll-name id]
  (fn [{:keys [request body]}]
    (let [db-instance (db-fn request)
          oid (->oid id)]
      (mc/update-by-id db-instance coll-name oid body))))

(defn- malformed-fn
  [saveable-mapper validator id]
  (fn [{:keys [request]}]
    (if (or (= :post (:request-method request))
            (= :put (:request-method request)))
      (let [body (:body request)
            acceptable? (map? body)]
        (if-not acceptable?
          [true {:error "Body malformed."}]
          (let [acceptable-body (process saveable-mapper body)
                id-equal? (= id (:_id acceptable-body))]
            (if-not id-equal?
              [true {:error "Body _id is not the same with the target url."}]
              (let [error (validate validator acceptable-body)]
                (if (nil? error)
                  [false {:body acceptable-body
                          :body-type :single}]
                  [true {:error error}]))))))
      [false {}])))

(defn- delete-entity-fn
  [db-fn coll-name id]
  (fn [{:keys [request]}]
    (let [db-instance (db-fn request)
          oid (->oid id)]
      (mc/remove-by-id db-instance coll-name oid))))

(defn- base-retrieve-update-delete-resource
  [& {:keys [get-entity-fn
             put-entity-fn
             delete-entity-fn
             malformed-fn
             exists-fn]}]
  {:available-media-types ["application/json"]
   :allowed-methods [:get :put :delete]
   :handle-ok #(get-entity-fn %)
   :handle-malformed :error
   :handle-not-found :entity
   :malformed? #(malformed-fn %)
   :exists? #(exists-fn %)
   :can-put-to-missing? false
   :put! #(put-entity-fn %)
   :delete! #(delete-entity-fn %)})

(defn retrieve-update-delete-resource
  [db-fn coll-name jsonable-mapper saveable-mapper validator id]
  (base-retrieve-update-delete-resource
   :get-entity-fn (get-entity-fn jsonable-mapper)
   :put-entity-fn (put-entity-fn db-fn coll-name id)
   :delete-entity-fn (delete-entity-fn db-fn coll-name id)
   :malformed-fn (malformed-fn saveable-mapper validator id)
   :exists-fn (exists-fn db-fn coll-name id)))
