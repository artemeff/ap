(ns ap.api
  (:require [ap.utils :as u]
            [cljs-http.client :as http]))

; http://ws.audioscrobbler.com/2.0/?method=track.getInfo&artist=cher&track=believe&format=json

;; data

(def vk-api-uri     "https://api.vk.com/method/")
(def lastfm-api-uri "http://ws.audioscrobbler.com/2.0/")
(def lastfm-api-key js/lastfmApiKey)

;; functions

(defn lastfm-track [artist title]
  (http/get lastfm-api-uri
            {:query-params {:method "track.getInfo"
                            :api_key lastfm-api-key
                            :artist artist
                            :track title
                            :format "json"}}))

; https://api.vk.com/method/METHOD_NAME?PARAMETERS&access_token=ACCESS_TOKEN

(defn vk-tracks [id token]
  (http/jsonp (str vk-api-uri "audio.get")
              {:query-params {:owner_id id
                              :access_token token}}))
