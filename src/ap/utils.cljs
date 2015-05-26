(ns ap.utils
  (:require [clojure.string :as string]))

(defn log [obj]
  (.log js/console obj))

(def screen-width
  (aget js/screen "width"))

(def screen-height
  (aget js/screen "height"))

(defn params->any [delim delim-kv m]
  (string/join delim
               (for [[k v] m]
                 (str (name k) delim-kv v))))

(defn params->ps [m]
  (params->any "," "=" m))

(defn params->qs [m]
  (params->any "&" "=" m))

(defn qs->params [str]
  (->> (string/split str #"&") 
       (map #(string/split % #"=")) 
       (map (fn [[k v]] [(keyword k) v])) 
       (into {})))

(defn open-window [url]
  (set! js/window.location.href url))

(defn css-image-url [url]
  (str "url(" url ")"))
