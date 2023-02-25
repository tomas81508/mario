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
        y (get-in db [:mario :y])]
    (cond (or (pos? y)
              (not (zero? vy)))
          (let [gravity 0.001
                new-y (min 0 (- y (* vy delta)))
                new-vy (if (zero? new-y) 0 (- vy (* gravity delta)))]
            (-> db
                (assoc-in [:mario :y] new-y)
                (assoc-in [:mario :vy] new-vy)))

          (contains? (:directions db) "ArrowUp")
          (assoc-in db [:mario :vy] 0.6)

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
   :mario      {:x         10
                :vx        0
                :y         0
                :vy        0
                :direction :right}})

