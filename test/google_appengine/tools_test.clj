(ns google-appengine.tools-test
  (:use :reload google-appengine
        (google-appengine tools users app-test))
  (:use [compojure.http.servlet :only (servlet)])
  (:require [compojure.server.jetty :as jetty]))

(init-appengine
 :app-dir "/tmp"
 :resources-dir "/home/haru/Sources/google-appengine/test/google_appengine/resources")

(def-appengine-server gae-server 8080
  "/*" (servlet (wrap-with-appengine app)))

(jetty/start gae-server)
(comment (jetty/stop gae-server))
