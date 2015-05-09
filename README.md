# enigma

A collection of utility to treat map as an Object.

## TODO

Update readme on how to create custom field and rule.

## Usage

* Validating

```clj
(ns foobar.core
  (:require [enigma.doc.field :as field])
  (:use [enigma.doc.validator :only [defvalidator]])
        enigma.doc.validator.core)
        
(defvalidator person-validator
  :name field/string-field
  :age field/int-field)
  
(validate person-validator  {})
;; {:name "Value cannot be nil.", :age "Value cannot be nil."}

(validate person-validator {:name "foo"})
;; {:age "Value cannot be nil."}

(validate person-validator {:name "foo" :age 100})
;; nil

(validate person-validator {:name "foo" :dont-want-this-key nil})
;; {:strict-error [:dont-want-this-key], :age "Value cannot be nil."}

(defvalidator non-strict-person-validator
  {:strict? false}
  :name field/string-field
  :age field/string-field)

(validate person-validator {:now-im-flexible true :age 100})
;; {:name "Value cannot be nil."}
```

* Updating value in a map 

```clj
(ns foobar.core 
  (:use [enigma.doc.mapper :only [process defmapper]]))
  
(defmapper greetings-mapper
  {:hobby true}
  :name (fn [_ v]
          (str "Hello my name is " v))
  :age (fn [_ v]
         (str "I'm " v " years old"))
  :hobby (fn [_ v]
           (if-not (nil? v)
             (str "I like " v)
             "I like to code!")))
             
(process greetings-mapper {})     
;; {:hobby "I like to code!"}

(process greetings-mapper {:name "foobar"
                           :age 15
                           :unchanged "jolly good"})
;; {:name "Hello my name is foobar"
    :age "I'm 15 years old"
    :hobby "I like to code!
    :unchaned "jolly good""}
```

## License

Copyright © 2015 Faris Nasution <faris.nasution156@gmail.com>

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
