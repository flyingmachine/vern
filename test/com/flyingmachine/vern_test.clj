(ns com.flyingmachine.vern-test
  (:require [com.flyingmachine.vern :refer :all])
  (:use midje.sweet))

(fact "it names entities"
  (name-entities [:test {:a 1} :best {:c 3} {:d 4}])
  => [{:name :test :data {:a 1}}
      {:name :best :data {:c 3}}
      {:name nil   :data {:d 4}}])

(fact "it retrieves loaded values"
  (replace-in {:test {:a 1}}
              {:b [:test :a]})
  => {:b 1})

(fact "if val is not a vector, use val"
  (replace-in {:test {:a 1}}
              {:b 2})
  => {:b 2})

(fact "it merges a common map"
  (merge-common [[{:a 1} {:b 2} {:a 2}]
                 :x
                 {:b 1}])
  => [{:a 1 :b 2} {:a 2}
      :x
      {:b 1}])

(let [data [:endpoints
             [:wallace
              {:name "wallace" :id 1}

              :gromit
              {:name "gromit"  :id 2}]

             :permissions
             [[{:endpoint [:endpoints :wallace]}

               :wallace-read
               {:name "read"  :id 10}

               :wallace-write
               {:name "write" :id 11}]

              [{:endpoint [:endpoints :gromit]}

               :gromit-read
               {:name "read"  :id 12}

               :gromit-write
               {:name "write" :id 13}]]]]

  (fact "it returns a map of named entities"
    (do-named (fn [processed group-name entity]
                (get-in entity [:data :id]))
              data)
    => {:endpoints {:wallace 1 :gromit 2}
        :permissions {:wallace-read 10
                      :wallace-write 11
                      :gromit-read 12
                      :gromit-write 13}})
  (fact "it expands entities with merges and associations"
    (let [entities (atom [])]
      (do-named (fn [processed group-name entity]
                  (swap! entities #(conj % (:data entity)))
                  (get-in entity [:data :id]))
                data)
      @entities => [{:id 1 :name "wallace"}
                    {:id 2 :name "gromit"}
                    {:id 10 :endpoint 1 :name "read"}
                    {:id 11 :endpoint 1 :name "write"}
                    {:id 12 :endpoint 2 :name "read"}
                    {:id 13 :endpoint 2 :name "write"}])))
