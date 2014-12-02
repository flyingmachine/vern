(defproject com.flyingmachine/vern "0.1.0-SNAPSHOT"
  :description ""
  :url ""
  :license {:name "The MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]]

  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[midje "1.5.0"]]}})
