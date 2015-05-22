(ns ap.auth
  (:require [ap.utils :as u]))

;; data

(def vk-client-id js/vkClientId)
(def redirect-url js/redirectUrl)

(def oauth-url "https://oauth.vk.com/authorize")

(def params {:client_id     vk-client-id
             :scope         "audio"
             :redirect_uri  redirect-url
             :display       "popup"
             :response_type "token"})

(def auth-url
  (str oauth-url "?" (u/params->qs params)))

;; functions

(defn open-auth-window []
  (u/open-window auth-url))

(defn auth-from-location-hash []
  (let [hash (aget js/window.location "hash")]
    (u/qs->params (subs hash 1))))

(defn authenticated? []
  ; (some #(when-not (empty? %) %) [(auth-from-location-hash auth-from-localstorage)])
  (not (empty? (auth-from-location-hash))))
