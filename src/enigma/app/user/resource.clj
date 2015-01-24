(ns enigma.app.user.resource
  (:require [enigma.resource [list :as rl]
                             [single :as sl]]
            [enigma.app.user [mapper :as um]
                             [validator :as uv]]
            [monger.collection :as mc]
            [buddy.hashers.bcrypt :as hs]
            [buddy.sign.generic :as sg])
  (:use [buddy.auth :only [authenticated? throw-unauthorized]]
        [enigma.resource.util :only [body-type]]
        [enigma.doc.validator.core :only [validate]]
        [enigma.doc.mapper :only [process]]))

(defn login-resource
  [db-fn coll-name privkey]
  {:available-media-types ["application/json"]
   :allowed-methods [:post]
   :handle-malformed :error
   :malformed? (fn [{:keys [request]}]
                 (let [current-type (body-type request)]
                   (if-not (= :single current-type)
                     [true {:error "Body malformed."}]
                     (let [body (:body request)
                           error (validate uv/login-validator identity)]
                       (if (nil? error)
                         [false {:body body}]
                         [true {:error error}])))))
   :handle-created (fn [{:keys [token]}]
                     {:token token})
   :authorized? (fn [{:keys [request] :as ctx}]
                  (if (authenticated? request)
                    [false {:error "Logged-in users can't login again."}]
                    (let [db-instance (db-fn request)
                          eml (get-in ctx [:body "email"])
                          pswd (get-in ctx [:body "password"])
                          user (mc/find-one-as-map db-instance
                                                   coll-name
                                                   {:email eml})]
                      (if (or (nil? user)
                              (not (hs/check-password pswd
                                                      (:password user))))
                        [false {:error "Wrong password or email."}]
                        [true {:token (sg/dumps user privkey)}]))))
   :handle-unauthorized (fn [_]
                          (throw-unauthorized))})

(defn update-password-resource
  [db-fn coll-name]
  {:available-media-types ["application/json"]
   :allowed-methods [:put]
   :handle-malformed :error
   :malformed? (fn [{:keys [request]}]
                 (let [current-type (body-type request)]
                   (if-not (= :single current-type)
                     [true {:error "Body malformed."}]
                     (let [body (:body request)
                           error (validate uv/update-password-validator
                                           identity)]
                       (if (nil? error)
                         [false {:body body}]
                         [true {:error error}])))))
   :handle-created (fn [{:keys [entity]}]
                     (um/user->jsonable entity))
   :authorized? (fn [{:keys [request] :as ctx}]
                  (if-not (authenticated? request)
                    [false {:error "Users must be logged-in."}]
                    (let [db-instance (db-fn request)
                          {:keys [new-password old-password]} (:body ctx)
                          email (get-in request [:identity :email])
                          user (mc/find-one-as-map db-instance
                                                   coll-name
                                                   {:email email})]
                      (if-not (hs/check-password old-password
                                                 (:password user))
                        [false {:error "Password didn't match."}]
                        (let [new-user (assoc user
                                         :password
                                         (hs/make-password new-password))
                              _ (mc/update-by-id db-instance
                                                 coll-name
                                                 (:_id user)
                                                 user)]
                          [true {:entity user}])))))
   :handle-unauthorized (fn [_]
                          (throw-unauthorized))})

(defn base-list-create-user-resource
  [db-fn coll-name page per-page order]
  (rl/list-create-resource db-fn
                           coll-name
                           um/user->jsonable
                           um/user->saveable
                           uv/user-validator
                           page
                           per-page
                           order))

(defn base-retrieve-upate-delete-user-resource
  [db-fn coll-name id]
  (sl/retrieve-update-delete-resource db-fn
                                      coll-name
                                      um/user->jsonable
                                      um/user->saveable
                                      uv/user-validator
                                      id))

;; status code untuk

;; berhasil login: 200 ok
;; gagal login: 422 unprocessable entity

;; berhasil register: 201 created
;; gagal register: 409 conflict

;; berhasil logout: 200 ok
;; gagal logout: 400 bad request
