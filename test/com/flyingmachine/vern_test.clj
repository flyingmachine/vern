(ns com.flyingmachine.vern-test
  (:require [com.flyingmachine.vern :refer :all])
  (:use midje.sweet))

(fact "it names entities dawg"
  (name-entities [:test {:a 1} :best {:c 3} {:d 4}])
  => [{:name :test
       :data {:a 1}}
      {:name :best
       :data {:c 3}}
      {:name nil
       :data {:d 4}}])

(fact "it retrieves loaded values"
  (retrieve-loaded-vals {:test {:a 1}}
                        {:b [:test :a]})
  => {:b 1})

(fact "if val is not a vector, use val"
  (retrieve-loaded-vals {:test {:a 1}}
                        {:b 2})
  => {:b 2})

(fact "it merges common entities"
  (merge-common [[{:a 1} {:b 2} {:a 2}]
                 [{:a 1} {:b 2} {:a 2}]
                 :x
                 {:b 1}])
  => [{:a 1 :b 2} {:a 2}
      {:a 1 :b 2} {:a 2}
      :x
      {:b 1}])

(fact "it returns a map of named entities"
  (do-named (fn [processed group-name entity]
              (get-in entity [:data :id]))
            [:endpoints
             [:wallace
              {:name "wallace"
               :id 1}

              :gromit
              {:name "gromit"
               :id 2}]

             :permissions
             [[{:endpoint [:endpoints :wallace]}

               :wallace-read
               {:name "read"
                :id 10}

               :wallace-write
               {:name "write"
                :id 11}]

              [{:endpoint [:endpoints :gromit]}

               :gromit-read
               {:name "read"
                :id 12}

               :gromit-write
               {:name "write"
                :id 13}]]])
  => {:endpoints {:wallace 1 :gromit 2}
      :permissions {:wallace-read 10
                    :wallace-write 11
                    :gromit-read 12
                    :gromit-write 13}})
