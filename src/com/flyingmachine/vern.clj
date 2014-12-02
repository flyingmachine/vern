(ns com.flyingmachine.vern)

(defn mapval
  [f m]
  (if (empty? m)
    m
    (reduce into (map (fn [[k v]] {k (f k v)}) m))))

(defn assoc-processed-vals
  [processed x]
  (mapval (fn [k v] (if (vector? v) (get-in processed v) v))
          x))

(defn name-entities
  "Look for keys preceding maps"
  [entities]
  (loop [remaining entities
         named []
         name nil]
    (if (empty? remaining)
      named
      (let [head (first remaining)]
        (if (keyword? head)
          (recur (rest remaining) named head)
          (recur (rest remaining) (conj named {:name name :data head}) nil))))))

(defn merge-common
  [entities]
  (->> entities
       (map (fn [x]
              (if (not (vector? x))
                x
                (let [common (first x)]
                  (map (fn [data-or-name]
                         (if (keyword? data-or-name)
                           data-or-name
                           (merge common data-or-name)))
                       (rest x))))))
       flatten
       (into [])))

(defn do-named
  [f data]
  (reduce (fn [processed [group-name entities]]
            (reduce (fn [processed entity]
                      (let [result (f processed group-name (update-in entity [:data] #(assoc-processed-vals processed %)))]
                        (if-let [entity-name (:name entity)]
                          (assoc-in processed [group-name entity-name] result)
                          processed)))
                    processed
                    (name-entities (merge-common entities))))
          {}
          (partition-all 2 data)))
