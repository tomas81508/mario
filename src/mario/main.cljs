(ns ^:figwheel-hooks mario.main
  (:require [reagent.core]
            [reagent.dom]
            [mario.view :refer [app-component]]
            [mario.db :refer [db-atom]]
            [mario.events :refer [handle-event]]
            [mario.tick :refer [tick-atom update-tick]]
            [mario.core :as core]))


(defn now []
  (-> (js/Date.) .getTime))


(defn render
  []
  (reagent.dom/render [app-component]
                      (js/document.getElementById "app")))

(def arrow-keys #{"ArrowLeft" "ArrowRight" "ArrowUp" "ArrowDown"})

(when-not (deref db-atom)
  (render)
  (reset! db-atom core/initial-state)
  (add-watch tick-atom :app-watcher
             (fn [_ _ _ {delta :delta}]
               (swap! db-atom core/update-model delta)))
  (js/setInterval (fn []
                    (swap! tick-atom update-tick (now)))
                  20)
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
