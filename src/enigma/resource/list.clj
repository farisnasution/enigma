(ns enigma.resource.list
  (:require [monger [query :as mq]
                    [collection :as mc]])
  (:use [enigma.util :only [->int]]
        [enigma.resource.util :only [body-type]]))

(def -GETs-default-settings
  {:page 0
   :per-page 20
   :order 1})

(defn- page-good?
  [page]
  (and (integer? page)
       (or (pos? page)
           (zero? page))))

(defn- per-page-good?
  [per-page]
  (page-good? per-page))

(defn- order-good?
  [order]
  (or (= order 1)
      (= order -1)))

(defn- check-all-value
  [default user params]
  (fn [f k]
    (let [from-params (->int (get params k))
          from-user (->int (get user k))
          from-default (get default k)]
      (if (f from-params)
        from-params
        (if (f from-user)
          from-user
          from-default)))))

(defn- extract-query
  [default user]
  (fn [request]
    (let [params (:params request)
          kwargs (dissoc params [:page :per-page :order])
          checker-fn (check-all-value default user params)
          current-page (checker-fn page-good? :page)
          current-per-page (checker-fn per-page-good? :per-page)
          current-order (checker-fn order-good? :order)]
      [kwargs  current-order [current-page current-per-page]])))

(defn malformed-fn
  [saveable-mapper validator]
  (fn [{:keys [request]}]
    (let [current-type (body-type request)
          body (:body request)]
      (condp = current-type
        :single (let [acceptable-body (saveable-mapper body)
                      error (validator acceptable-body)]
                  (if (nil? error)
                    [false {:body acceptable-body
                            :body-type :single}]
                    [true {:error error}]))
        :list (let [acceptable-bodies (map saveable-mapper body)
                    errors (map validator acceptable-bodies)]
                (if (every? nil? errors)
                  [false {:body acceptable-bodies
                          :body-type :list}]
                  [true {:error errors}]))
        nil [true {:error "Body malformed."}]))))

(defn- get-entity-fn
  [db-fn coll-name jsonable-mapper page per-page order]
  (let [default-opts -GETs-default-settings
        user-opts {:order order
                   :per-page per-page
                   :page page}
        query-extractor (extract-query default-opts user-opts)]
    (fn [{:keys [request]}]
      (let [[kwargs c-order [c-page c-per-page]] (query-extractor request)
            db-instance (db-fn request)
            query-result (mq/with-collection db-instance coll-name
                           (mq/find kwargs)
                           (mq/sort {:_id c-order})
                           (mq/paginate :page c-page :per-page c-per-page))]
        (map jsonable-mapper query-result)))))

(defn- post-entity-fn
  [db-fn coll-name]
  (fn [{:keys [body body-type request]}]
    (let [db-instance (db-fn request)]
      (condp = body-type
        :single (mc/insert db-instance coll-name body)
        :list (mc/insert-batch db-instance coll-name body)))))

(defn- base-list-create-resource
  [& {:keys [get-entity-fn
             post-entity-fn
             malformed-fn]}]
  {:available-media-types ["application/json"]
   :allowed-methods [:get :post]
   :handle-ok #(get-entity-fn %)
   :handle-malformed :error
   :post! #(post-entity-fn %)
   :malformed? #(malformed-fn %)})

(defn list-create-resource
  [db-fn coll-name j-mapper s-mapper validator page per-page order]
  (base-list-create-resource :get-entity-fn (get-entity-fn db-fn
                                                           coll-name
                                                           j-mapper
                                                           page
                                                           per-page
                                                           order)
                             :post-entity-fn (post-entity-fn db-fn coll-name)
                             :malformed-fn (malformed-fn s-mapper
                                                         validator)))
