(ns xwingdata.data
   (:require [clojure.data.json :as json]
            [clojure.java.io :as io]))
   
(def upgrades (json/read-str (slurp (io/resource "data/upgrades.js")) :key-fn keyword))
(def sources (json/read-str (slurp (io/resource "data/sources.js")) :key-fn keyword))

(defn- slot-upgrades [slotname]
   (->> upgrades
       (filter #(= (:slot %) slotname))
       (sort-by :name)
       (sort-by :points)))

(defn get-source-counts [upgradeid]
   (->> sources 
       (map #(get-in % [:contents :upgrades (keyword (str upgradeid))]))
       (filter identity)
       (reduce +)))
       
(defn- add-source-counts [upgrades]
   (map #(assoc % :count (get-source-counts (:id %))) upgrades))

(defn get-upgrade-sets [upgradeid]
   (->> sources 
       (filter #(get-in % [:contents :upgrades (keyword (str upgradeid))])) 
       (mapv :name)))   
       
(defn- add-source-sets [upgrades]
   (map #(assoc % :sourcesets (get-upgrade-sets (:id %))) upgrades))

(defn custom-upgrade-list [slotname]
   (->> (slot-upgrades slotname)
        add-source-counts
        add-source-sets))
       
(defn update-upgrades [xwd slotname]
   (assoc xwd :upgrades (custom-upgrade-list slotname)))
  
(defn xwd []
   {:slots (sort (distinct (map #(:slot %) upgrades)))
    :upgrades (custom-upgrade-list "Astromech")})
    
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