# Clojure library for Google App Engine

This library is a Clojure API for starting local development [Google
App Engine](http://code.google.com/appengine) server from REPL. It is
heavily refactored code from Hacker's with Attitude gist and Official
App Engine documentation.

### Install

[Leiningen](http://github.com/technomancy/leiningen) usage:
    :dependencies [[org.clojure/clojure "1.2.0-master-SNAPSHOT"]
                   [org.clojure/clojure-contrib "1.2.0-SNAPSHOT"]
                   [compojure "0.3.2"]
                   [google-appengine "0.1-SNAPSHOT"]
                   [com.google.appengine/appengine-api-1.0-sdk "1.3.4"]
                   [com.google.appengine/appengine-api-labs "1.3.4"]
                   [com.google.appengine/appengine-api-stubs "1.3.4"]]
    :dev-dependencies [[lein-clojars "0.5.0"]
                       [com.google.appengine/appengine-local-runtime "1.3.4"]
                       [com.google.appengine/appengine-testing "1.3.4"]]
    :repositories [["maven-gae-plugin" "http://maven-gae-plugin.googlecode.com/svn/repository"]]

### google-app-engine.local

API of the google-app-engine.local package providing starting local
jetty server to develop GAE application interactively in
REPL. Authorization on local server with GAE SDK Users API works fine.

    (ns offline
      (:use :reload app google-app-engine.tools)
      (:use [compojure.http.servlet :only (servlet)])
      (:require [compojure.server.jetty :as jetty]))

    (init-appengine :app-dir "/tmp"
                    :resources-dir "/home/haru/Sources/app/war/resources")

    (def-appengine-server local-gae-server 8080
      "/*" (servlet (wrap-with-appengine app)))

    (jetty/start local-gae-server)
    (jetty/stop  local-gae-server)

Static resource files must be saved in <code>war/resources/</code>
directory. To access them from code use links like
<code>href="/resources/css/stylesheet.css"</code>.

Project directory structure:

    app
    src
      app
      offline
    war
      WEB-INF
        classes
        lib
        appengine-web.xml
        web.xml
      resources
        ...
        css
          ...
          stylesheet.css
        img
          ...
          favicon.img

---

Copyright (c) 2010 Roman Zaharov <zahardzhan@gmail.com>

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
