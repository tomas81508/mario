(ns mario.view
  (:require [mario.db :refer [db-atom]]))

(def sky-height 500)

(defn get-mario-image-src
  [mario]
  (str "asset/"
       (cond (not (zero? (:vy mario)))
             (if (= (:direction mario) :left) "left-jump" "right-jump")

             (zero? (:vx mario))
             (if (= (:direction mario) :left) "left" "right")

             :else
             (if (= (:direction mario) :left) "left-walk" "right-walk"))))

(defn mario-component
  [db]
  (let [mario-x (get-in db [:mario :x])
        mario-y (get-in db [:mario :y])
        mario-image-height 70
        mario-image-padding-bottom 8
        mario-image-padding-left 25]
    [:img {:src   (str (get-mario-image-src (:mario db)) ".gif")
           :style (merge {:position         "absolute"
                          :top              (+ sky-height
                                               mario-image-padding-bottom
                                               (- mario-y
                                                  mario-image-height))
                          :left             (- mario-x
                                               mario-image-padding-left)
                          :transform-origin "0 0"
                          :transform        (str "scale(2)")}
                         (when (:frames db)
                           {:border           "1px solid red"}))}]))

(defn app-component
  []
  (let [db (deref db-atom)
        width (* (quot (:screen-width db) 50) 50)]
    [:div {:style {:position "relative"}}
     [:div {:style {:position "absolute"
                    :z-index  1}}
      (->> (for [x (range 0 width 50)
                 y (range 0 500 50)]
             [x y])
           (map-indexed (fn [index [x y]]
                          (let [box [x (- sky-height y)]]
                            [:div {:key      index
                                   :style    (merge {:position "absolute"
                                                     :width    50
                                                     :height   50
                                                     :top      y
                                                     :left     x}
                                                    (when (:frames db)
                                                      {:border   "1px solid gray"})
                                                    (when (contains? (:boxes db) box)
                                                      {:background-color "brown"}))
                                   :on-click (fn []
                                               (let [operation (if (contains? (:boxes db) box) disj conj)]
                                                 (swap! db-atom update :boxes operation box)))}]))))]
     [:div {:style {:position "relative"}}
      [mario-component db]
      [:div {:id       "the-sky"
             :style    {:background-color "rgb(174, 238, 238)"
                        :height           "500px"
                        :width            "100%"}}]
      [:div {:style {:background-color "rgb(74, 163, 41)"
                     :height           "100px"
                     :width            "100%"}}]]]))
