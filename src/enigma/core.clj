(ns enigma.core
  (:require [buddy.sign.jws :as jws]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.auth.backends.token :refer [signed-token-backend parse-authorization-header]]
            [buddy.auth.backends.session :refer [session-backend]]
            [buddy.sign.generic :refer [loads dumps]]
            [buddy.auth :refer [authenticated? throw-unauthorized]]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(def q (dumps {:username "faris"
                  :_id 1}
                 "my secret key"))

q

;; (jws/unsign q "my secret key")

(defn handler
  [request]
  request
  ;; (if-not (authenticated? request)
  ;;   (throw-unauthorized)
  ;;   request)
  )

(defn uh
  [request metadata]
  "gagal!")

(def backend (signed-token-backend {:unauthorized-handler uh
                                    :privkey "my secret key"
                                    :max-age (* 15 60)}))

(def s-backend (session-backend {:unauthorized-handler uh}))

(def wrapped (-> handler
                 (wrap-authentication backend)
                 (wrap-authorization backend)
                 ))

(wrapped {:headers {"authorization" (str "Token " "faris")}})

(loads q "my secret key" {:max-age (* 15 60)})

(parse-authorization-header {:headers {"authorization" (str "Token " "faris")}})

;; kerja praktek - kapsel - pptb - nirkabel
(/ (+ (* 2 4) (* 2 3.5) (* 3 4) (* 3 2)) 10)

;; kerja praktek - kapsel - pptb - nirkabel - mri
(/ (+ (* 2 4) (* 2 3.5) (* 3 4) (* 3 2) (* 2 4)) 12)

;; kerja praktek - kapsel - pptb - nirkabel - mri - siskomul
(/ (+ (* 2 4) (* 2 3.5) (* 3 4) (* 3 2) (* 2 4) (* 3 4)) 15)

;; protocolnya ValidationFn
;; protocol berisi -> validate, construct
;; validate untuk nge validasi
;; construct buat ngerubah setting nya
;; record punya body, settings
;; recordnya adalah -> Rule, Rules, Validator

;; (deffield string-field
;;   required
;;   string-only
;;   not-empty
;;   max-length
;;   min-length
;;   [{:keys [minn maxx required blank]} rules]
;;   (construct max-length {:min-length }))
