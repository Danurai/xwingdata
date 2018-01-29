(ns xwingdata.client
   (:require [reagent.core :as r]
            [chord.client :refer [ws-ch]]
            [cljs.core.async :refer [<! >! put! close!]])
   (:require-macros [cljs.core.async.macros :refer [go go-loop]])
   (:refer-clojure :exclude [uuid? uri]))
   
(goog-define ws-uri "ws://localhost:9009/ws")   ;;https://www.martinklepsch.org/posts/parameterizing-clojurescript-builds.html
(enable-console-print!)

;; Sample data for testing   
(def app-data (r/atom {}))

;; Page Functions
(defn get-upgrade-data [id]
   (first (filter #(= (:id %) id) (:upgrades @app-data) )))

(defn show-data [id]
   #(swap! app-data assoc 
      :image (str "/images/" (:image (get-upgrade-data id))) 
      :sourcesets (:sourcesets (get-upgrade-data id))))
   
;; Render Page
(defn render-breadcrumbs [ws-ch]
   [:div {:class "row"}
      [:div {:class "col-sm-12"}
         [:ul {:class "breadcrumb"}
            (for [crd (:slots @app-data)]
               ^{:key crd} [:li {:on-click #(put! ws-ch crd)} crd])]]])

(defn render-list []
   [:div {:class "col-sm-6"}
      [:ul {:class "list-group-small"}
         (for [ug (:upgrades @app-data)]
            ^{:key (:id ug)}
               [:li {:class "list-group-item" 
                    :on-mouse-over (show-data (:id ug))
                    :on-click (show-data (:id ug))} 
                    (str (if (= (:unique ug) true) "\u2022 ") (:name ug) " (" (:points ug) ")")
                    [:span {:class "badge"} (:count ug)]])]])               
                       
(defn render-detail []
   [:div {:class (str "col-sm-6 sticky"
                    (if-not (:image @app-data) " hide"))}
      [:img {:src (str (:image @app-data))
            :class "img-small"}]
      [:h3 "Source sets"]
      (for [src (:sourcesets @app-data)]
         ^{:key src} [:p src])])                       
                       
(defn render-content []
   [:div {:class "row"}
      (render-list)
      (render-detail)])
      
(defn Page [ws-ch]
   [:div {:class "container-fluid container-fix"}
      (render-breadcrumbs ws-ch)
      (render-content)])

(go
  (let [{:keys [ws-channel error]} (<! (ws-ch ws-uri))]  ;; Set up websocket  (get (System/getenv) "WS-CHAN" "ws://localhost:9009/ws")
    (if-not error
      (do 
         (r/render [Page ws-channel] (.getElementById js/document "app"))
         ;;(>! ws-channel "Astromech")
         (loop []
            (when-let [{:keys [message]} (<! ws-channel)]   ;;Error handling for server going away
               (reset! app-data message)
               ;;(swap! app-data assoc :slots (:slots message))
               ;;(swap! app-data assoc :upgrades (:upgrades message))
               (recur)))) 
      (js/console.log "Error:" (pr-str error)))))
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
