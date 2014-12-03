# vern

vern is a tiny library that I originally used to write and load
database fixtures. Its purpose is to allow you to compactly represent
entities and to allow entity attributes to reference the return value
of a previously-processed entity. Here's the general form of the
fixtures:

```clojure
[:entity-group-key
 [:entity-1-key ;; entity keys are optional
  {:name "zip"}

  ;; using a sequential structure allows you to reference
  ;; another entity
  {:parent-id [:entity-group-key :entity-1-key]
   :name "zip's child 1"}

  ;; you can define common associations by grouping entities in a
  ;; sequential; the first element contains the common associations
  [{:parent-id [:entity-group-key :entity-1-key]}
   {:name "zip's child 2"}
   {:name "zip's child 3"}]]

 :entity-group-2
 [{:entity-group-1-id [:entity-group-key :entity-1-key]}]]
```

Here's an example fixture:

```clojure
[:endpoints
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
   {:name "write" :id 13}]]]
```

And here's an example of processing the fixture:

```clojure
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
              {:name "write" :id 13}]]]
      entities (atom [])]
  (do-named (fn [processed group-name entity]
              (swap! entities #(conj % (:data entity)))
              (get-in entity [:data :id]))
            data)
  @entities)
; =>
[{:id 1 :name "wallace"}
 {:id 2 :name "gromit"}
 {:id 10 :endpoint 1 :name "read"}
 {:id 11 :endpoint 1 :name "write"}
 {:id 12 :endpoint 2 :name "read"}
 {:id 13 :endpoint 2 :name "write"}]
```
