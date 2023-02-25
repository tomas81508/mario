(ns mario.core)

(defn maybe-move-mario-x
  [db delta]
  (let [vx (get-in db [:mario :vx])
        velocity 0.2]
    (cond (contains? (:directions db) "ArrowLeft")
          (update db :mario
                  (fn [mario]
                    (-> mario
                        (update :x + (* delta vx))
                        (assoc :vx (- velocity)
                               :direction :left))))

          (contains? (:directions db) "ArrowRight")
          (update db :mario
                  (fn [mario]
                    (-> mario
                        (update :x + (* delta vx))
                        (assoc :vx velocity
                               :direction :right))))

          (zero? vx) db

          :else
          (update db :mario
                  (fn [mario]
                    (-> mario
                        (update :vx (fn [vx] (if (neg? vx)
                                               (min 0 (+ vx (* delta 0.001)))
                                               (max 0 (- vx (* delta 0.001))))))
                        (update :x + (* delta vx))))))))

(defn maybe-move-mario-y
  [db delta]
  (let [vy (get-in db [:mario :vy])
        y (get-in db [:mario :y])
        x (get-in db [:mario :x])
        magic 45
        boxes (:boxes db)]
    (cond (and (contains? (:directions db) "ArrowUp")
               (zero? vy))
          (-> db
              (assoc-in [:mario :vy] 0.6)
              (update-in [:mario :y] (fn[v] (- v 0.6))))

          (or (neg? y)
              (not (zero? vy)))
          (let [gravity 0.001
                new-y (min 0 (- y (* vy delta)))
                new-vy (if (zero? new-y) 0 (- vy (* gravity delta)))
                colliding-boxes (->> boxes
                                     (filter (fn [[bx by]]
                                               (and
                                                 (<= (- bx 30) x (+ bx 45))
                                                 (<= (- new-y) (+ by magic) (- y))))))]
            (when (> (count colliding-boxes) 0) (println "boxy"))

            (if (empty? colliding-boxes)
              (-> db
                  (assoc-in [:mario :y] new-y)
                  (assoc-in [:mario :vy] new-vy))
              (-> db
                  (assoc-in [:mario :y] (-> colliding-boxes
                                            (first)
                                            (second)
                                            (-)
                                            (- magic)
                                            ))
                  (assoc-in [:mario :vy] 0)
                  )))

          (contains? (:directions db) "ArrowUp")
          (-> db
              (assoc-in [:mario :vy] 0.6)
              (update-in [:mario :y] (fn[v] (- v 0.6))))

          (and (zero? y)
               (not (zero? vy)))
          (assoc-in db [:mario :vy] 0)

          :else
          db)))

(defn maybe-move-mario
  [db delta]
  (-> db
      (maybe-move-mario-x delta)
      (maybe-move-mario-y delta)))

(defn update-model
  [db delta]
  (-> db
      (maybe-move-mario delta)))

(def initial-state
  {:directions #{}
   :boxes      #{}
   :mario      {:x         10
                :vx        0
                :y         0
                :vy        0
                :direction :right}})

