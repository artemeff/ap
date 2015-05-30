(ns ap.api
  (:require [ap.utils :as u]
            [goog.net.Jsonp :as jsonp])
  (:import [goog.net Jsonp]))

; http://ws.audioscrobbler.com/2.0/?method=track.getInfo&artist=cher&track=believe&format=json

;; data

(def vk-api-uri     "https://api.vk.com/method/")
(def lastfm-api-uri "http://ws.audioscrobbler.com/2.0/")
(def lastfm-api-key js/lastfmApiKey)

;; functions

(defn- fetch [uri success-fn error-fn]
  (.send (Jsonp. uri) nil
         #(success-fn (js->clj % :keywordize-keys true))
         #(error-fn (js->clj % :keywordize-keys true))))

(defn- lastfm-track-uri [artist title]
  (let [params {:method  "track.getInfo"
                :api_key lastfm-api-key
                :artist  artist
                :track   title
                :format  "json"}]
    (str lastfm-api-uri "?" (u/params->qs params))))

(defn lastfm-track [artist title success-fn error-fn]
  (fetch (lastfm-track-uri artist title) success-fn error-fn))

(defn- vk-tracks-uri [id token]
  (let [params {:owner_id     id
                :access_token token}]
    (str vk-api-uri "audio.get" "?" (u/params->qs params))))

(defn vk-tracks [id token success-fn error-fn]
  (fetch (vk-tracks-uri id token) success-fn error-fn))
