(defproject enigma "0.1.0"
  :description "HTTP Server library"
  :url "https://github.com/farisnasution/enigma"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [liberator "0.12.2"]
                 [com.novemberain/monger "2.0.1"]
                 [buddy "0.2.3"]]
  :profiles {:dev {:dependencies [[speclj "3.1.0"]]
                   :plugins [[jonase/eastwood "0.2.1"]
                             [lein-ancient "0.6.7"]
                             [lein-kibit "0.1.2"]
                             [lein-bikeshed "0.2.0"]]}}
  :plugins [[speclj "3.1.0"]]
  :test-paths ["spec"]
  :aliases {"dev" ["repl" ":headless"]
            "omni" ["do" ["ancient"] ["kibit"] ["eastwood"] ["bikeshed"]]
            "au" ["ancient" "ugprade" ":all"]}
  :eastwood {:exclude-linters [:unlimited-use]
             :exclude-namespaces [:test-paths]})
