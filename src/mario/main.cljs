(ns ^:figwheel-hooks mario.main
  (:require [reagent.core]
            [reagent.dom]
            [mario.view :refer [app-component]]
            [mario.db :refer [db-atom]]
            [mario.events :refer [handle-event]]
            [mario.tick :refer [tick-atom update-tick]]
            [mario.core :as core]))


(defn now [] (-> (js/Date.) .getTime))

(defn render
  []
  (reagent.dom/render [app-component]
                      (js/document.getElementById "app")))

(def arrow-keys #{"ArrowLeft" "ArrowRight" "ArrowUp" "ArrowDown"})

(when-not (deref db-atom)
  (render)
  (reset! db-atom (merge core/initial-state
                         {:screen-width (.-innerWidth js/window)
                          :frames       false
                          :boxes        core/clojure-boxes}))
  (add-watch tick-atom :app-watcher
             (fn [_ _ _ {delta :delta}]
               (swap! db-atom core/update-model delta)))
  (js/setInterval (fn [] (swap! tick-atom update-tick (now)))
                  20)
  (.addEventListener js/window
                     "resize"
                     (fn [] (swap! db-atom assoc :screen-width (.-innerWidth js/window))))
  (.addEventListener (.-body js/document)
                     "keyup"
                     (fn [e]
                       (let [key (.-key e)]
                         (when (contains? arrow-keys key)
                           (handle-event {:name :key-up :data key})))))
  (.addEventListener (.-body js/document)
                     "keydown"
                     (fn [e]
                       (let [key (.-key e)]
                         (when (contains? arrow-keys key)
                           (handle-event {:name :key-down :data key})))))
  )

(defn after-load
  {:after-load true}
  []
  (println "After load triggered")
  (render)
  )
