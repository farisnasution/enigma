(ns enigma.app.user.auth
  (:use [buddy.auth :only [authenticated? throw-unauthorized]]))

(defn- allowed-by-roles?
  [roles user]
  (boolean (some roles (:roles user))))

(defn auth-handler
  ([roles]
   {:authorized? (fn [ctx]
                   (authenticated? (:request ctx)))
    :handle-unauthorized (fn [_] (throw-unauthorized))
    :allowed? (fn [ctx]
                (allowed-by-roles? roles
                                   (get-in ctx [:request :identity])))
    :handle-forbidden (fn [_] (throw-unauthorized))})
  ([]
   (role-based-authorization #{:user})))
