(ns xwingdata.core-test
   (:require [expectations :refer :all] 
            [xwingdata.data :refer :all]))
(expect ["Astromech" "Bomb" "Cannon" "Cargo" "Crew" "Elite" 
           "Hardpoint" "Illicit" "Missile" "Modification" "Salvaged Astromech" 
           "System" "Team" "Tech" "Title" "Torpedo" "Turret"]
       (:slots (xwd)))
       
 (expect 6
      (-> (xwd)
          (update-upgrades "Turret")
          :upgrades
          count))
 ;; helper - get upgrade id         
 (defn get-upgrade-id [upgrade-name]
   (:id (first (filter #(= (:name %) upgrade-name) upgrades))))
 (expect 1
   (get-upgrade-id "Proton Torpedoes"))
   
;; Search sets
(expect 6
   (reduce + (filter identity (map #(get-in % [:contents :upgrades :1]) sources))))
   
(expect ("Core")
   (filter #(get-in % [:contents :upgrades :1]) sources))