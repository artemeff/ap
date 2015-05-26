(ns ap.utils
  (:require [clojure.string :as string]))

(defn log [obj]
  (.log js/console obj))

(def screen-width
  (aget js/screen "width"))

(def screen-height
  (aget js/screen "height"))

(defn params->any
  ([delim delim-kv m] (params->any delim delim-kv m #(%)))
  ([delim delim-kv m fn-kv]
   (string/join delim
                (for [[k v] m]
                  (str (fn-kv (name k)) delim-kv (fn-kv v))))))

(defn params->ps [m]
  (params->any "," "=" m))

(defn params->qs [m]
  (params->any "&" "=" m #(js/encodeURIComponent %)))

(defn qs->params [str]
  (->> (string/split str #"&") 
       (map #(string/split % #"=")) 
       (map (fn [[k v]] [(keyword k) v])) 
       (into {})))

(defn open-window [url]
  (set! js/window.location.href url))

(defn css-image-url [url]
  (str "url(" url ")"))
