(defproject ap "0.1.0-SNAPSHOT"
  :description "Vk Audio Player"
  :url "http://ap.artemeff.com"

  :dependencies [[org.clojure/clojure "1.7.0-beta3"]
                 [org.clojure/clojurescript "0.0-3269"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [sablono "0.3.4"]
                 [org.omcljs/om "0.8.8"]
                 [alandipert/storage-atom "1.2.4"]
                 [sablono "0.3.4"]]

  :plugins [[lein-cljsbuild "1.0.6"]
            [lein-figwheel "0.3.3"]
            [lein-kibit "0.1.2"]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled"
                                    "target"]
  
  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]

              :figwheel {:on-jsload "ap.core/on-js-reload"}

              :compiler {:main ap.core
                         :asset-path "js/compiled/out"
                         :output-to "resources/public/js/compiled/ap.js"
                         :output-dir "resources/public/js/compiled/out"
                         :source-map-timestamp true}}
             {:id "min"
              :source-paths ["src"]
              :compiler {:output-to "resources/public/js/compiled/ap.min.js"
                         :main ap.core                         
                         :optimizations :advanced
                         :pretty-print false}}]}

  :figwheel {
             ;; :http-server-root "public" ;; default and assumes "resources" 
             ;; :server-port 3449 ;; default
             :css-dirs ["resources/public/css"] ;; watch and update CSS

             ;; Start an nREPL server into the running figwheel process
             ;; :nrepl-port 7888

             ;; Server Ring Handler (optional)
             ;; if you want to embed a ring handler into the figwheel http-kit
             ;; server, this is for simple ring servers, if this
             ;; doesn't work for you just run your own server :)
             ;; :ring-handler hello_world.server/handler

             ;; To be able to open files in your editor from the heads up display
             ;; you will need to put a script on your path.
             ;; that script will have to take a file path and a line number
             ;; ie. in  ~/bin/myfile-opener
             ;; #! /bin/sh
             ;; emacsclient -n +$2 $1
             ;;
             ;; :open-file-command "myfile-opener"

             ;; if you want to disable the REPL
             ;; :repl false

             ;; to configure a different figwheel logfile path
             ;; :server-logfile "tmp/logs/figwheel-logfile.log" 
             })
