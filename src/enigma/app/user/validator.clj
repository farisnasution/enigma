(ns enigma.app.user.validator
  (:require [enigma.doc.field :as field])
  (:use [enigma.doc.validator :only [defvalidator]]
        [enigma.app.base.validator :only [base-validator]]
        enigma.doc.validator.core))

(defvalidator user-validator
  base-validator
  :username (construct field/string-field {:min-length? 5
                                           :max-length? 100})
  :password field/string-field
  :slug field/slug-field
  :email field/email-field
  :roles field/set-field)

(defvalidator login-validator
  :email field/email-field
  :password field/string-field)

(defvalidator update-password-validator
  :new-password field/string-field
  :old-password field/string-field)
