(ns xwingdata.core
  (:require
    [cljs.core.async :as async :refer (<! >! put! chan)]
    [taoensso.sente  :as sente :refer (cb-success?)]
    [reagent.core :as r])
  (:require-macros
    [cljs.core.async.macros :as asyncm :refer (go go-loop)]))

; sente
(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/chsk" ; Note the same path as before
       {:type :auto ; e/o #{:auto :ajax :ws}
       })]
  (def chsk       chsk)
  (def ch-chsk    ch-recv) ; ChannelSocket's receive channel
  (def chsk-send! send-fn) ; ChannelSocket's send API fn
  (def chsk-state state)   ; Watchable, read-only atom
  )
  
(enable-console-print!)

(def app-data (r/atom nil))

(defn get-upgrades [ev crd] 
  (chsk-send! 
    [:core/breadcrumb crd]
    5000
    (fn [cb-reply]
      (swap! app-data assoc :upgrades cb-reply)
      (swap! app-data dissoc :image :sourcesets)
      )))

(defn get-upgrade-data [id] 
  (->> @app-data 
      :upgrades 
      (filter #(= (:id %) id))
      first))
   ;(first (filter #(= (:id %) id) (:upgrades @app-data) )))
   
(defn show-data [id]
  (swap! app-data assoc 
    :image (str "/images/" (:image (get-upgrade-data id))) 
    :sourcesets (:sourcesets (get-upgrade-data id))))

(defn xwing-breadcrumbs []
  [:div.row   
    [:ol.breadcrumb
      (doall (for [crd (:slots @app-data)]
        ^{:key crd} [:li.breadcrumb-item 
                    {:class (if (= (-> @app-data :upgrades first :slot) crd) "active")
                     :on-click #(get-upgrades % crd)} crd]))]])
                  
(defn xwing-data []
  [:div.row
    [:div.col-sm-6 
      [:ul.list-group-small
        (for [ug (:upgrades @app-data)]
          ^{:key (:id ug)} [:li.list-group-item.d-flex.justify-content-between.align-items-center
            {:on-mouse-over  #(show-data (:id ug))
             :on-click      #(show-data (:id ug))}
            (str (if (true? (:unique ug)) "\u26AB ") (:name ug) " (" (:points ug) ")")
          [:span.badge.badge-secondary.badg-pill (:count ug)]])]]
    [:div.col-sm-6.sticky {:hidden (nil? (:image @app-data))}
      [:img.img-small {:src (str (:image @app-data))}]
      [:h3 "Source sets"]
        (for [src (:sourcesets @app-data)]
           ^{:key src} [:p src])]
  ])

(defn Page []
  [:div.container.container-fix
    [xwing-breadcrumbs]
    [xwing-data]])

(r/render [Page] (.getElementById js/document "app"))

; initialise upgrade list

; sente listener for message on websocket
;
(defmulti event-msg-handler :id)

(defmethod event-msg-handler :default  [{:as ev-msg :keys [?data]}] nil)

(defmethod event-msg-handler :chsk/state
  [{:as ev-msg :keys [?data]}]
    (let [[old-state-map new-state-map] ?data]
    (if (:first-open? new-state-map)
      (chsk-send! [:core/initialise] 5000 
        (fn [cb-reply] (reset! app-data cb-reply))))))
;
;(defmethod event-msg-handler :default [{:as ev-msg :keys [event]}]
;  (println "Unhandled event: %s" event))
;  
;(defmethod event-msg-handler :chsk/state [{:as ev-msg :keys [?data]}]
;  (if (= ?data {:first-open? true})
;    (println "Channel socket successfully established!")
;    (println "Channel socket state change:" ?data)))
;
(def router
  (sente/start-client-chsk-router! ch-chsk event-msg-handler))
;