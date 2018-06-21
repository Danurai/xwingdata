(ns xwingdata.web
  (:require [compojure.core :refer [defroutes GET POST]]
           [compojure.route :refer [resources]]
           
           (ring.middleware
            [session :refer [wrap-session]]
            [params :refer [wrap-params]]
            [keyword-params :refer [wrap-keyword-params]]
            [anti-forgery :refer [wrap-anti-forgery]])
           
           [taoensso.sente :as sente]
           [taoensso.sente.server-adapters.http-kit      :refer [get-sch-adapter sente-web-server-adapter]]
           [hiccup.page :as h]
           [xwingdata.data :as data]))
 
(def appdata (atom #{:notnil})) 

(let [{:keys [ch-recv 
            send-fn
            connected-uids
            ajax-post-fn
            ajax-get-or-ws-handshake-fn]}
     (sente/make-channel-socket! (get-sch-adapter) {})]

  (def ring-ajax-post                ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake   ajax-get-or-ws-handshake-fn)
  (def ch-chsk                      ch-recv) ; ChannelSocket's receive channel
  (def chsk-send!                   send-fn) ; ChannelSocket's send API fn
  (def connected-uids                connected-uids) ; Watchable, read-only atom
  )
  
(defn landingpage [req]
  (h/html5
    [:head
    ;; Meta Tags
      [:meta {:charset "UTF-8"}]
      [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
      [:link {:rel "icon" :href "xwingdata-icon-32.png"}]
    ;; jquery & bootstrap
      [:script {:src "https://code.jquery.com/jquery-3.3.1.min.js" :integrity "sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" :crossorigin "anonymous"}]
      [:link   {:rel "stylesheet" :href "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" :integrity "sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" :crossorigin "anonymous"}]
      [:script {:src "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" :integrity "sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" :crossorigin "anonymous"}]
      (h/include-css "https://fonts.googleapis.com/css?family=Orbitron")
      (h/include-css "/css/style.css")]
    [:body [:div#app [:div.loader]]
      (h/include-js "js/compiled/xwingdata.js")]))

(defroutes approutes
  (GET "/" [] landingpage)
  
 ; sente 
  (GET  "/chsk" req (ring-ajax-get-or-ws-handshake req))
  (POST "/chsk" req (ring-ajax-post              req))
;  (GET  "/chsk" req ((:ring-ajax-get-or-ws-handshake channel-socket) req))
;  (POST "/chsk" req ((:ring-ajax-post              channel-socket) req))
  
  (resources "/"))
  
(def app
  (-> approutes
    (wrap-keyword-params)
    (wrap-params)
    (wrap-anti-forgery)
    (wrap-session)))
    
(defmulti event :id)

(defmethod event :default [{:as ev-msg :keys [event ?reply-fn]}]
  (prn event))
  
(defmethod event :core/initialise [{:as ev-msg :keys [event ?reply-fn]}]
  (prn event)
  (when ?reply-fn
    (?reply-fn (data/xwd))))
  
(defmethod event :core/breadcrumb [{:as ev-msg :keys [event ?reply-fn]}]
  (prn event)
  (when ?reply-fn
    (?reply-fn (-> event last data/custom-upgrade-list))))
  
(defmethod event :chsk/ws-ping [_])
    
(defonce router
  (sente/start-chsk-router! ch-chsk event))
  
  
;(defmethod event :core/b1 [{:as ev-msg :keys [event ?reply-fn]}]
;  (prn event)
;  (doseq [uid (:any @connected-uids)]
;    (chsk-send! uid [:some/broadcast {:what-is-this "An async broadcast pushed from server to all clients"}]))
;  (when ?reply-fn 
;    (?reply-fn "Hello from server")))