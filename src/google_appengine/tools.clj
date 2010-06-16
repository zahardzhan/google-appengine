(ns google-appengine.tools
  (:use :reload google-appengine.util)
  
  (:use clojure.contrib.def)
  (:use [compojure.http.servlet :only (servlet defservice)]
        [compojure.http.helpers :only (serve-file)]
        compojure.http.routes)
  
  (:require [compojure.server.jetty :as jetty])
  
  (:import java.io.File
           com.google.apphosting.api.ApiProxy
           (com.google.appengine.tools.development ApiProxyLocalFactory
                                                   ApiProxyLocalImpl
                                                   LocalServerEnvironment)
           (com.google.appengine.api.users dev.LocalLogoutServlet
                                           dev.LocalLoginServlet)
           (com.google.appengine.tools.development.testing LocalServiceTestHelper
                                                           LocalServiceTestConfig
                                                           LocalBlobstoreServiceTestConfig
                                                           LocalDatastoreServiceTestConfig
                                                           LocalImagesServiceTestConfig
                                                           LocalMailServiceTestConfig
                                                           LocalMemcacheServiceTestConfig
                                                           LocalTaskQueueTestConfig
                                                           LocalURLFetchServiceTestConfig
                                                           LocalUserServiceTestConfig
                                                           LocalXMPPServiceTestConfig)))

(defn local-service-test-helper [& configs]
  (new LocalServiceTestHelper (into-array LocalServiceTestConfig configs)))

(defvar- within-local-service-block false)

(defmacro with-local-service [service & body]
  (if within-local-service-block
    `(do ~@body)

    `(binding [within-local-service-block true]
       (try (.setUp ~service)
            (do ~@body)
            (finally (.tearDown ~service))))))

(defvar- default-port 8080)
(defvar- default-app-dir ".")
(defvar- default-resources-dir "/tmp")
(defvar- local-development-environment nil)

(defn init-appengine [& {:as opts :keys [app-dir app-id attributes auth-domain email
                                        admin? logged-in? port request-namespace
                                        resources-dir version-id]
                        :or {port          default-port
                             app-dir       default-app-dir
                             resources-dir default-resources-dir}}]
  {:pre  [(when-supplied attributes    (instance? java.util.Map attributes)
                         auth-domain   (instance? String auth-domain)
                         email         (email? email)
                         admin?        (instance? Boolean admin?)
                         logged-in?    (instance? Boolean logged-in?)
                         request-namespace (instance? String request-namespace)
                         version-id    (instance? String version-id))]}
  (let [app-dir             (as-file app-dir)
        resources-dir       (as-file resources-dir)
        local-service       (local-service-test-helper
                             (new LocalBlobstoreServiceTestConfig)
                             (new LocalDatastoreServiceTestConfig)
                             (new LocalImagesServiceTestConfig)
                             (new LocalMailServiceTestConfig)
                             (new LocalMemcacheServiceTestConfig)
                             (new LocalTaskQueueTestConfig)
                             (new LocalURLFetchServiceTestConfig)
                             (new LocalUserServiceTestConfig)
                             (new LocalXMPPServiceTestConfig))
        api-proxy-local (. (new ApiProxyLocalFactory) create
                           (proxy [LocalServerEnvironment] []
                             (getAppDir [] app-dir)
                             (getAddress [] "localhost")
                             (getPort [] port)
                             (waitForServerToStart [] nil)))]
    (defvar- local-development-environment
      {:local-service-test-helper local-service
       :api-proxy-local           api-proxy-local
       :port                      port
       :app-dir                   app-dir
       :resources-dir             resources-dir})
    (do (when-supplied app-id      (. local-service setEnvAppId app-id)
                       attributes  (. local-service setEnvAttributes
                                      (merge attributes
                                             {"com.google.appengine.server_url_key"
                                              (str "http://localhost:" port)}))
                       auth-domain (. local-service setEnvAuthDomain auth-domain)
                       email       (. local-service setEnvEmail email)
                       admin?      (. local-service setEnvIsAdmin admin?)
                       logged-in?  (. local-service setEnvIsLoggedIn logged-in?)
                       request-namespace (. local-service setEnvRequestNamespace request-namespace)
                       version-id  (. local-service setEnvVersionId version-id)))))

(defn appengine-initialized? []
  (not= nil local-development-environment))

(defvar- within-dev-env-block false)

(defmacro with-dev-env [& body]
  (if within-dev-env-block
    `(do ~@body)

    `(binding [within-dev-env-block true]
       (try (do (.setUp (:local-service-test-helper local-development-environment))
                (ApiProxy/setDelegate (:api-proxy-local local-development-environment)))
            (do ~@body)
            (finally (do (.tearDown (:local-service-test-helper local-development-environment))
                         ;; (.stop (ApiProxy/getDelegate))
                         ))))))

(defn- parse-dev-appserver-login-cookie
  "LocalLoginServlet set cookie:
  'dev_appserver_login=test@example.com:false:18580476422013912411'"
  [cookie]
  (or (and (string? cookie)
           (let [[_ email _ _ auth-domain _ _ admin?]
                 (re-find #"dev_appserver_login=(([_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*)@([A-Za-z0-9]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{2,}))):(true|false)"
                          cookie)
                 admin? (Boolean. admin?)]
             (when (and (email? email) auth-domain)
               {:email email :auth-domain auth-domain :admin? admin? :logged-in? true})))
      {:email nil :auth-domain nil :admin? false :logged-in? false}))

(defn wrap-with-appengine "Wraps a ring app with local App Engine
  environment. Sign in App Engine when LocalLoginServlet set up
  cookie. Sign out when LocalLogoutServlet reset cookie."
  [app] (fn [request]
          (do (let [{:keys [email auth-domain admin? logged-in?]}
                    (parse-dev-appserver-login-cookie ((request :headers) "cookie"))

                    local-service (:local-service-test-helper local-development-environment)]
                (. local-service setEnvAuthDomain auth-domain)
                (. local-service setEnvEmail      email)
                (. local-service setEnvIsAdmin    admin?)
                (. local-service setEnvIsLoggedIn logged-in?)))
          (with-dev-env (app request))))

(defn- handle-resource-file-local [path filename]
  (fn [request]
    {:body (serve-file path filename)
     :headers {"Content-Type" (or (mime-types (second (re-find #"\.([a-z]+)$" filename)))
                                  "application/octet-stream")}}))

(defn- resources-route [url-pattern]
  (GET url-pattern
       (or (handle-resource-file-local (str (:resources-dir local-development-environment))
                                       (params :*))
           :next)))

(defroutes resources-routes
  (resources-route "/resources/*"))

(defmacro def-appengine-server [name port & servlets]
  `(jetty/defserver ~name {:port ~port :join? true}
     "/_ah/login"   (new LocalLoginServlet)
     "/_ah/logout"  (new LocalLogoutServlet)
     "/resources/*" (servlet resources-routes)
     ~@servlets))
