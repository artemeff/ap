(ns ^:figwheel-always ap.core
  (:require [ap.auth :as a]
            [ap.api :as api]
            [ap.utils :as u]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [sablono.core :as html :refer-macros [html]]))

(enable-console-print!)

;; utils

(def default-cover "/img/default_cover.jpg")
(def default-cover-bg "/img/bg.jpg")

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

(defn log [obj]
  (.log js/console obj))

(defn access-token []
  (:access_token (:user @app-state)))

(defn user-id []
  (:user_id (:user @app-state)))

(defn set-user! [credentials]
  (swap! app-state assoc :user credentials))

(defn set-current-track! [track]
  (swap! app-state assoc :current-track track))

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
      (log (api/vk-tracks (user-id) (access-token)))
      (set-cover-as-bg! track)
      (html [:div.current-track
             (display-track-cover track)
             (display-track track)]))))

(defn player [data]
  (reify
    om/IRender
    (render [this]
      (html [:div#player
             [:div.counter (:counter data)]
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
