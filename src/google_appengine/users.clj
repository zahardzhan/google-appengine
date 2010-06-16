(ns google-appengine.users
  (:use google-appengine.util)
  (:require google-appengine)
  (:use clojure.contrib.def)
  (:import (com.google.appengine.api.users UserServiceFactory
                                           UserService
                                           User)))

(defvar *user-service*)
(defvar *current-user*)

;; class MyHandler(webapp.RequestHandler):
;;     def get(self):
;;         user = users.get_current_user()
;;         if user:
;;             greeting = ("Welcome, %s! (<a href=\"%s\">sign out</a>)" %
;;                         (user.nickname(), users.create_logout_url("/")))
;;         else:
;;             greeting = ("<a href=\"%s\">Sign in or register</a>." %
;;                         users.create_login_url("/"))

;;         self.response.out.write("<html><body>%s</body></html>" % greeting)
