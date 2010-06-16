(defproject google-appengine "0.1-SNAPSHOT"
  :author "Roman Zaharov <zahardzhan@gmail.com>"
  :description "Clojure library for Google App Engine."
  :url "http://github.com/zahardzhan/google-appengine"

  :autodoc {:name "Google App Engine"
            :copyright "Copyright (c) 2010 Roman Zaharov <zahardzhan@gmail.com>"}
  
  :dependencies [[org.clojure/clojure "1.2.0-master-SNAPSHOT"]
                 [org.clojure/clojure-contrib "1.2.0-SNAPSHOT"]
                 [compojure "0.3.2"]
                 [com.google.appengine/appengine-api-1.0-sdk "1.3.4"]
                 [com.google.appengine/appengine-api-labs "1.3.4"]
                 [com.google.appengine/appengine-api-stubs "1.3.4"]
                 [com.google.appengine/appengine-local-runtime "1.3.4"]
                 [com.google.appengine/appengine-testing "1.3.4"]]

  :dev-dependencies [[inflections "0.4-SNAPSHOT"]
                     [hiccup "0.2.4"]
                     [lein-clojars "0.5.0"]
                     [swank-clojure "1.2.1"]
                     [autodoc "0.7.0"]]

  :repositories [["maven-gae-plugin" "http://maven-gae-plugin.googlecode.com/svn/repository"]]

  :namespaces [google-appengine
               google-appengine.tools
               google-appengine.users
               google-appengine.util])
