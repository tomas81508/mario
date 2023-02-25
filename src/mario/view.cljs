(ns mario.view
  (:require [mario.db :refer [db-atom]]))

(defn get-mario-image-src
  [mario]
  (str "asset/"
       (cond (neg? (:y mario))
             (if (= (:direction mario) :left) "left-jump" "right-jump")

             (zero? (:vx mario))
             (if (= (:direction mario) :left) "left" "right")

             :else
             (if (= (:direction mario) :left) "left-walk" "right-walk"))))

(defn app-component
  []
  (let [db (deref db-atom)
        mario-x (get-in db [:mario :x])
        mario-y (get-in db [:mario :y])]
    [:div
     [:div (str "Directions: " (:directions db))]
     [:div (str "Mario: " (:mario db))]
     [:div
      [:img {:src   (str (get-mario-image-src (:mario db)) ".gif")
             :style {:position  "absolute"
                     :transform (str "scale(2) "
                                     "translate(" mario-x "px, " (+ 228 mario-y) "px)")}}]
      [:div {:style {:background-color "rgb(174, 238, 238)"
                     :height           "500px"
                     :width            "100%"}}]
      [:div {:style {:background-color "rgb(74, 163, 41)"
                     :height           "100px"
                     :width            "100%"}}]]]))
