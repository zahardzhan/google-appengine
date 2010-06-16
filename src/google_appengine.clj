(ns google-appengine
  (:use     :reload google-appengine.util)
  (:require [google-appengine.users :as users])
  (:use clojure.contrib.def))

(defvar- within-appengine-block false)

(defn within-appengine-block? []
  within-appengine-block)

(defmacro with-appengine "Execute body within App Engine environment." 
  [& body]
  (if within-appengine-block
    `(do ~@body)

    `(binding [within-appengine-block true]
       (with-bindings {#'users/*user-service* nil
                       #'users/*current-user* nil}
         (do ~@body)))))
