(ns ap.api
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [ap.utils :as u]
            [cljs.core.async :refer [<! put! chan]])
  (:import [goog.net Jsonp]))

; http://ws.audioscrobbler.com/2.0/?method=track.getInfo&artist=cher&track=believe&format=json

;; data

(def vk-api-uri     "https://api.vk.com/method/")
(def lastfm-api-uri "http://ws.audioscrobbler.com/2.0/")
(def lastfm-api-key js/lastfmApiKey)

;; functions

(defn fetch [uri]
  (let [out (chan)
        req (Jsonp. uri "callback")]
    (.send req nil #(put! out %))
    out))

(defn lastfm-track-uri [artist title]
  (let [params {:method  "track.getInfo"
                :api_key lastfm-api-key
                :artist  artist
                :track   title
                :format  "json"}]
    (str lastfm-api-uri "?" (u/params->qs params))))

(defn lastfm-track-handle [response]
  (let [data  (js->clj response :keywordize-keys true)
        track (:track data)
        album (:album track)]
    (u/log (last (:image album)))))

(defn lastfm-track [artist title]
  (go (lastfm-track-handle (<! (fetch (lastfm-track-uri artist title))))))
