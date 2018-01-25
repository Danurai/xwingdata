(ns user
  (:require [reloaded.repl :refer [system reset stop]]
           [xwingdata.system]))

(reloaded.repl/set-init! #'xwingdata.system/create-system)