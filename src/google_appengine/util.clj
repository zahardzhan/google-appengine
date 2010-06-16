(ns google-appengine.util
  (:import java.io.File))

(defmacro when-supplied [& clauses]
  (if (not clauses) true
      `(and (or (nil? ~(first clauses))
                (do ~(second clauses)))
            (when-supplied ~@(next (next clauses))))))

(defmacro with-return
  [expr & body]
  `(do (do ~@body)
       ~expr))

(defmacro let-return 
  [[form val] & body]
  `(let [~form ~val]
     (with-return ~form (do ~@body))))

(defmulti as-file type)

(defmethod as-file String [x]
  (File. x))

(defn email? [#^String email]
  (string? (first (re-find #"^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\.[A-Za-z0-9]+)*(\.[A-Za-z]{2,})$"
                           email))))

(def mime-types
  {"ai"    "application/postscript"
   "asc"   "text/plain"
   "avi"   "video/x-msvideo"
   "bin"   "application/octet-stream"
   "bmp"   "image/bmp"
   "class" "application/octet-stream"
   "cer"   "application/pkix-cert"
   "crl"   "application/pkix-crl"
   "crt"   "application/x-x509-ca-cert"
   "css"   "text/css"
   "dms"   "application/octet-stream"
   "doc"   "application/msword"
   "dvi"   "application/x-dvi"
   "eps"   "application/postscript"
   "etx"   "text/x-setext"
   "exe"   "application/octet-stream"
   "gif"   "image/gif"
   "htm"   "text/html"
   "html"  "text/html"
   "jpe"   "image/jpeg"
   "jpeg"  "image/jpeg"
   "jpg"   "image/jpeg"
   "js"    "text/javascript"
   "lha"   "application/octet-stream"
   "lzh"   "application/octet-stream"
   "mov"   "video/quicktime"
   "mpe"   "video/mpeg"
   "mpeg"  "video/mpeg"
   "mpg"   "video/mpeg"
   "pbm"   "image/x-portable-bitmap"
   "pdf"   "application/pdf"
   "pgm"   "image/x-portable-graymap"
   "png"   "image/png"
   "pnm"   "image/x-portable-anymap"
   "ppm"   "image/x-portable-pixmap"
   "ppt"   "application/vnd.ms-powerpoint"
   "ps"    "application/postscript"
   "qt"    "video/quicktime"
   "ras"   "image/x-cmu-raster"
   "rb"    "text/plain"
   "rd"    "text/plain"
   "rtf"   "application/rtf"
   "sgm"   "text/sgml"
   "sgml"  "text/sgml"
   "swf"   "application/x-shockwave-flash"
   "tif"   "image/tiff"
   "tiff"  "image/tiff"
   "txt"   "text/plain"
   "xbm"   "image/x-xbitmap"
   "xls"   "application/vnd.ms-excel"
   "xml"   "text/xml"
   "xpm"   "image/x-xpixmap"
   "xwd"   "image/x-xwindowdump"
   "zip"   "application/zip"})
