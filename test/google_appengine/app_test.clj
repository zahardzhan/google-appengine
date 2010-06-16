(ns google-appengine.app-test
  (:gen-class :extends javax.servlet.http.HttpServlet)
  (:use  [compojure.http.servlet :only (defservice)]
         compojure.http.routes
         (hiccup core page-helpers form-helpers))
  (:import (com.google.appengine.api.users UserServiceFactory
                                           UserService
                                           User)))

(defmacro html-head [& body]
  `(html [:head
          [:meta {:http-equiv "content-type" :content "text/html; charset=utf-8"}]
          [:link {:rel "stylesheet"          :href "/resources/css/stylesheet.css" :type "text/css"}]
          [:link {:rel "icon"                :href "/resources/img/favicon.png"    :type "image/png"}]
          ~@body]))

(defn index []
  (let [user-service (UserServiceFactory/getUserService)
        user (.getCurrentUser user-service)]
    (html [:html
           (html-head [:title "Тест"])
           [:body
            [:p (cond user  (html (link-to (.createLogoutURL user-service "/") "Sign out") \space
                                  (.getNickname user) \space
                                  (when (.isUserAdmin user-service) "Signed as admin"))
                      :else (html (link-to (.createLoginURL user-service "/") "Sign in")))]]])))

(defroutes app
  (GET "/favicon.ico" [] "/resources/img/favicon.png")
  (GET "/"            [] (index)))

(defservice app)
