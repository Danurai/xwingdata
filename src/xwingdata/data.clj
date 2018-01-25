(ns xwingdata.data
   (:require [clojure.data.json :as json]
            [clojure.java.io :as io]))
   
(def upgrades (json/read-str (slurp (io/resource "data/upgrades.js")) :key-fn keyword))
(def sources (json/read-str (slurp (io/resource "data/sources.js")) :key-fn keyword))

(defn prep [xwd]
   xwd
)

(defn- slot-upgrades [slotname]
   (sort-by :points (sort-by :name (filter #(= (:slot %) slotname) upgrades))))

(defn update-upgrades [xwd slotname]
   (assoc xwd :upgrades (slot-upgrades slotname)))
  
(defn xwd []
   {:slots (sort (distinct (map #(:slot %) upgrades)))
     :upgrades (slot-upgrades "Astromech")})
    
;;["Astromech" "Bomb" "Cannon" "Cargo" "Crew" "Elite" "Hardpoint" "Illicit" "Missile" "Modification" "Salvaged Astromech" "System" "Team" "Tech" "Title" "Torpedo" "Turret"]   
;;[{:name "BB-8" :image "upgrades/Astromech/bb-8.png" :id 1 :slot "Astromech"}
;; {:name "R2-D2" :image "upgrades/Astromech/r2-d2.png" :id 2 :slot "Astromech"}
;; {:name "R2 Astromech" :image "upgrades/Astromech/r2-astromech.png" :id 3 :slot "Astromech"}]
;; Upgrades
;;{
;; "name": "Ion Cannon Turret",
;; "id": 0,
;; "slot": "Turret",
;; "points": 5,
;; "attack": 3,
;; "range": "1-2",
;; "text": "<strong>Attack:</strong> Attack 1 ship (even a ship outside your firing arc).<br /><br />If this attack hits the target ship, the ship suffers 1 damage and receives 1 ion token.  Then cancel <strong>all</strong> dice results.",
;; "image": "upgrades/Turret/ion-cannon-turret.png",
;; "xws": "ioncannonturret"
;;}