(ns xwingdata.web
  (:require [chord.http-kit :refer [with-channel]]
           [clojure.java.io :as io]
           [compojure.core :refer [defroutes GET]]
           [compojure.route :refer [resources]]
           [clojure.core.async :refer [<! >! put! close! go go-loop]]
			  [xwingdata.data :refer [xwd update-upgrades prep]]
           ))

           
(defn ws-handler [req]
   (with-channel req ws-ch
      (go
         (loop [loopdata (xwd)]
            (>! ws-ch (prep loopdata))
            (when-let [{:keys [message]} (<! ws-ch)]
               (do 
                  (prn "Message received:" message)
                  (recur (update-upgrades loopdata message))))))))
		
(defroutes app
  (GET "/ws" [] ws-handler)
  (GET "/" [] (slurp (io/resource "public/index.html")))
  (resources "/"))