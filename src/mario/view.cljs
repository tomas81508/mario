(ns mario.view
  (:require [mario.db :refer [db-atom]]))

(defn get-mario-image-src
  [mario]
  (str "asset/"
       (cond (not (zero? (:vy mario)))
             (if (= (:direction mario) :left) "left-jump" "right-jump")

             (zero? (:vx mario))
             (if (= (:direction mario) :left) "left" "right")

             :else
             (if (= (:direction mario) :left) "left-walk" "right-walk"))))

(defn justify-coord
  [coord]
  (-> coord
      (quot 50)
      (* 50)))

(defn app-component
  []
  (let [db (deref db-atom)
        magic-number 458
        mario-image-padding 8
        mario-x (get-in db [:mario :x])
        mario-y (get-in db [:mario :y])]
    [:div {:style {:position "relative"}}
     [:div {:style {:position "absolute"
                    :z-index 1
                    :font-size "50px"
                    :transform "translateX(120px) translateY(210px)"}}
      "Nisse"]
     [:div {:style {:position "relative"}}
      (->> db
           (:boxes)
           (map-indexed (fn [idx [x y]]
                          [:svg
                           {:key      idx
                            :view-box "0 0 50 50"
                            :style    {:position "absolute"
                                       :pointer-events "none"
                                       :width    50
                                       :height   50
                                       :top      (- magic-number y)
                                       :left     x}}
                           [:rect {:x    0 :y 0 :width 50 :height 50
                                   :fill "brown"}]])))
      [:img {:src   (str (get-mario-image-src (:mario db)) ".gif")
             :style {:position  "absolute"
                     :top       (+ magic-number mario-y)
                     :left      mario-x
                     ;:border    "1px solid red"
                     :transform (str "scale(2)")}}]
      [:div {:id       "the-sky"
             :on-click (fn [e] (let [sky-y (-> "the-sky"
                                               (js/document.getElementById)
                                               (.getBoundingClientRect)
                                               (.-top))
                                     x (justify-coord (.-clientX e))
                                     y (- (justify-coord (- magic-number (.-clientY e) (- sky-y) -50))
                                          mario-image-padding)
                                     operation (if (contains? (:boxes db) [x y]) disj conj)]
                                   (swap! db-atom update :boxes operation [x y])))
             :style    {:background-color "rgb(174, 238, 238)"
                        :height           "500px"
                        :width            "100%"}}]
      [:div {:style {:background-color "rgb(74, 163, 41)"
                     :height           "100px"
                     :width            "100%"}}]]
     ;[:div (str "Directions: " (:directions db))]
     ;[:div (str "Mario: " (:mario db))]
     ;[:div (str "Boxes: " (:boxes db))]
     ]))
