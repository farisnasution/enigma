(ns enigma.util)

(defn now
  []
  (java.util.Date.))

(defn ->int
  [v]
  (try (Integer/parseInt v)
       (catch NumberFormatException error nil)
       (catch ClassCastException error nil)))

(defn contains-in? [m ks]
  (let [not-found (Object.)]
    (not-any? #{not-found}
              [(get-in m ks not-found)])))

(defn dissoc-in
  [m ks]
  (let [ks (vec ks)
        contains-in (contains-in? m ks)]
    (if contains-in
      (if (contains? ks 1)
        (let [update-key (pop ks)
              last-key (last ks)]
          (update-in m update-key (fn [current-m]
                                    (dissoc current-m last-key))))
        (dissoc m (first ks)))
      m)))
