(defproject enigma "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :profiles {:dev {:dependencies [[speclj "3.1.0"]]}}
  :plugins [[speclj "3.1.0"]]
  :test-paths ["spec"]
  :aliases {"dev" ["repl" ":headless"]
            "omni" ["do" ["ancient"] ["kibit"] ["eastwood"] ["bikeshed"]]}
  :eastwood {:exclude-linters [:unlimited-use]
             :exclude-namespaces [:test-paths]})
