(ns ^:figwheel-always ap.core
  (:require [ap.auth :as a]
            [ap.api :as api]
            [ap.utils :as u]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]))

(enable-console-print!)

;; utils

(def default-cover    "/img/default_cover.jpg")
(def default-cover-bg "/img/bg.jpg")
(def audio-player     "audio-player")

(defn el [id]
  (.getElementById js/document id))

;; app data

(defonce app-state
  (atom {:counter 0
         :user nil
         :playlist [{:artist "Massive Attack" :title "Teardrops" :cover default-cover}
                    {:artist "Atmosphere" :title "Trying to find a balance"}]
         :current-track nil}))

;; utils

(defn play! []
  (.play (el audio-player)))

(defn pause! []
  (.pause (el audio-player)))

(defn access-token []
  (:access_token (:user @app-state)))

(defn user-id []
  (:user_id (:user @app-state)))

(defn set-playlist! [tracks]
  (swap! app-state assoc :playlist tracks))

(defn set-user! [credentials]
  (swap! app-state assoc :user credentials))

(defn set-current-track! [track]
  (pause!)
  (swap! app-state assoc :current-track track)
  (play!))

(defn set-cover-as-bg! [track]
  (let [app-el (el "app")
        cover (or (:cover track) default-cover-bg)]
    (set! app-el.style.backgroundImage (u/css-image-url cover))))

;; html utils

(defn icon [name]
  [:span {:class (str "icon-" name)}])

(defn display-track [{:keys [artist title] :as track}]
  [:span.track
   [:span.track--artist artist]
   [:span.track--mdash "—"]
   [:span.track--title title]])

(defn display-track-cover [{:keys [cover] :as track}]
  (let [cur-cover (or cover default-cover)]
    [:span.cover [:img {:src cur-cover}]]))

;; jsonp callbacks

(defn lastfm-track-callback [{:keys [track]}]
  (let [image (last (:image (:album track)))]
    ))

(defn vk-tracks-callback [{:keys [response error] :as data}]
  (let [tracks (vec (rest response))]
    (set-playlist! tracks)))

;; components

(defn playlist-item [track owner]
  (reify
    om/IRender
    (render [this]
      (html [:div.playlist--item {:on-click #(set-current-track! track)}
             #_[:span.play-button (icon "play")]
             (display-track track)]))))

(defn playlist [playlist owner]
  (reify
    om/IRender
    (render [this]
      (html [:div.playlist
             (om/build-all playlist-item playlist)]))))

(defn current-track [track owner]
  (reify
    om/IRender
    (render [this]
      (api/lastfm-track (:artist track) (:title track) lastfm-track-callback #(u/error %))
      (set-cover-as-bg! track)
      (html [:div.current-track
             (display-track-cover track)
             (display-track track)]))))

(defn player [data]
  (reify
    om/IRender
    (render [this]
      ;; load user playlist
      (api/vk-tracks (user-id) (access-token) vk-tracks-callback #(u/error %))
      ;; render
      (html [:div#player
             [:div.counter (:counter data)]
             [:audio {:id audio-player}
              [:source {:src (:url (:current-track data)) :type "audio/mpeg"}]]
             (om/build playlist (:playlist data))
             (om/build current-track (:current-track data))]))))

(defn auth [data owner]
  (reify
    om/IRender
    (render [this]
      (html [:div.auth
             [:div.auth--btn
              {:on-click #(a/open-auth-window)}
              "Authenticate"]]))))

(defn app [data]
  (reify
    om/IRender
    (render [this]
      (if (a/authenticated?)
        (do
          (set-user! (a/auth-from-location-hash))
          (om/build player data))
        (om/build auth data)))))

;; main function

(om/root app app-state {:target (el "app")})

;; optional callback when js is reloaded

(defn on-js-reload []
  (swap! app-state update-in [:counter] inc)) 
