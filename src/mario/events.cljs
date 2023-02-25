(ns mario.events
  (:require [mario.db :refer [db-atom]]))

(defmulti handle-event
          "Event handler. Events should contain :name and :data."
          (fn [{name :name :as event}]
            (println "event:" event)
            name))

(defmethod handle-event
  :key-up
  [{data :data}]
  (swap! db-atom update :directions disj data))

(defmethod handle-event
  :key-down
  [{data :data}]
  (swap! db-atom update :directions conj data))