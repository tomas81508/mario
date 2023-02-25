(ns mario.tick
  (:require [reagent.core]))

(defonce tick-atom (reagent.core/atom nil))

(defn update-tick
  [tick time]
  (if-not tick
    {:time time :delta 0}
    {:time time :delta (- time (:time tick))}))

