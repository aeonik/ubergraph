(ns ubergraph.core-test
  (:require [clojure.test :refer :all]
            [ubergraph.core :refer :all]
            [ubergraph.protocols :refer :all]))

(deftest simple-graph-test
  (let [g1 (graph [1 2] [1 3] [2 3] 4)]
    (testing "Construction, nodes, edges"
      (are [expected got] (= expected got)
        #{1 2 3 4} (set (nodes g1))
        #{[1 2] [1 3] [2 3]} (set (for [e (edges g1)] [(src e) (dest e)]))
        true (has-node? g1 4)
        true (has-edge? g1 1 2)
        false (has-node? g1 5)
        false (has-edge? g1 4 1)))
    (testing "Successors"
      (are [expected got] (= expected got)
        #{2 3} (set (successors g1 1))
        #{1 2} (set (successors g1 3))
        #{} (set (successors g1 4))
        2 (out-degree g1 1)
        2 (out-degree g1 3)
        0 (out-degree g1 4)))
    (testing "Add & remove"
      (are [expected got] (= expected got)
        #{1 2 3 4 5} (set (nodes (add-nodes g1 5)))
        #{1 2} (set (nodes (remove-nodes g1 3 4)))
        #{[1 2]} (set (for [e (edges (remove-nodes g1 3 4))] [(src e) (dest e)]))
        #{1 2 3 4} (set (nodes (remove-edges g1 [1 2] [2 1] [1 3] [3 1])))
        #{[2 3]} (set (for [e (edges (remove-edges g1 [1 2] [2 1] [1 3] [3 1]))] [(src e) (dest e)]))))))

(defn vec-edges [g]
  (for [e (edges g)] [(src e) (dest e)]))

(deftest simple-digraph-test
  (let [g1 (digraph [1 2] [1 3] [2 3] 4)
        g6 (transpose g1)]
    (testing "Construction, nodes, edges"
      (are [expected got] (= expected got)
        #{1 2 3 4} (set (nodes g1))
        #{1 2 3 4} (set (nodes g6))
        #{[1 2] [1 3] [2 3]} (set (vec-edges g1))
        #{[2 1] [3 1] [3 2]} (set (vec-edges g6))
        true (has-node? g1 4)
        true (has-edge? g1 1 2)
        false (has-node? g1 5)
        false (has-edge? g1 2 1)))
    (testing "Successors"
      (are [expected got] (= expected got)
        #{2 3} (set (successors g1 1))
        #{} (set (successors g1 3))
        #{} (set (successors g1 4))
        2 (out-degree g1 1)
        0 (out-degree g1 3)
        0 (out-degree g1 4)
        #{1 2} (set (predecessors g1 3))
        #{} (set (predecessors g1 1))
        2 (in-degree g1 3)
        0 (in-degree g1 1)
        #{1 2} (set (successors g6 3))
        #{} (set (successors g6 1))
        2 (out-degree g6 3)
        0 (out-degree g6 1)))
    (testing "Add & remove"
      (are [expected got] (= expected got)
        #{1 2 3 4 5} (set (nodes (add-nodes g1 5)))
        #{1 2} (set (nodes (remove-nodes g1 3 4)))
        #{[1 2]} (set (vec-edges (remove-nodes g1 3 4)))
        #{1 2 3 4} (set (nodes (remove-edges g1 [1 2] [1 3])))
        #{[2 3]} (set (vec-edges (remove-edges g1 [1 2] [1 3])))))))

(deftest simple-weighted-graph-test
  (let [g1 (graph [1 2 77] [1 3 88] [2 3 99] 4)]
    (testing "Construction, nodes, edges"
      (are [expected got] (= expected got)
        #{1 2 3 4} (set (nodes g1))
        #{[1 2] [1 3] [2 3]} (set (vec-edges g1))
        true (has-node? g1 4)
        true (has-edge? g1 1 2)
        false (has-node? g1 5)
        false (has-edge? g1 4 1)))
    (testing "Successors"
      (are [expected got] (= expected got)
        #{2 3} (set (successors g1 1))
        #{1 2} (set (successors g1 3))
        #{} (set (successors g1 4))
        2 (out-degree g1 1)
        2 (out-degree g1 3)
        0 (out-degree g1 4)))
    (testing "Add & remove"
      (are [expected got] (= expected got)
        #{1 2 3 4 5} (set (nodes (add-nodes g1 5)))
        #{1 2} (set (nodes (remove-nodes g1 3 4)))
        #{[1 2]} (set (vec-edges (remove-nodes g1 3 4)))
        #{1 2 3 4} (set (nodes (remove-edges g1 [1 2] [2 1] [1 3] [3 1])))
        #{[2 3]} (set (vec-edges (remove-edges g1 [1 2] [2 1] [1 3] [3 1])))))
    (testing "Weight"
      (are [expected got] (= expected got)
        77 (weight g1 1 2)))))

(deftest simple-weighted-digraph-test
  (let [g1 (digraph [1 2 77] [1 3 88] [2 3 99] 4)
        g5 (digraph)
        g6 (transpose g1)]
    (testing "Construction, nodes, edges"
      (are [expected got] (= expected got)
        #{1 2 3 4} (set (nodes g1))
        #{1 2 3 4} (set (nodes g6))
        #{[1 2] [1 3] [2 3]} (set (vec-edges g1))
        #{[2 1] [3 1] [3 2]} (set (vec-edges g6))
        #{} (set (nodes g5))
        #{} (set (vec-edges g5))
        true (has-node? g1 4)
        true (has-edge? g1 1 2)
        false (has-node? g1 5)
        false (has-edge? g1 2 1)))
    (testing "Successors"
      (are [expected got] (= expected got)
        #{2 3} (set (successors g1 1))
        #{} (set (successors g1 3))
        #{} (set (successors g1 4))
        2 (out-degree g1 1)
        0 (out-degree g1 3)
        0 (out-degree g1 4)
        #{1 2} (set (predecessors g1 3))
        #{} (set (predecessors g1 1))
        2 (in-degree g1 3)
        0 (in-degree g1 1)
        #{1 2} (set (successors g6 3))
        #{} (set (successors g6 1))
        2 (out-degree g6 3)
        0 (out-degree g6 1)))
    (testing "Add & remove"
      (are [expected got] (= expected got)
        #{1 2 3 4 5} (set (nodes (add-nodes g1 5)))
        #{:a :b :c} (set (nodes (add-nodes g5 :a :b :c)))
        #{{:id 1} {:id 2}} (set (nodes (add-nodes g5 {:id 1} {:id 2})))
        #{[1 2]} (set (vec-edges (add-edges g5 [1 2])))
        #{1 2} (set (nodes (remove-nodes g1 3 4)))
        #{[1 2]} (set (vec-edges (remove-nodes g1 3 4)))
        #{1 2 3 4} (set (nodes (remove-edges g1 [1 2] [1 3])))
        #{[2 3]} (set (vec-edges (remove-edges g1 [1 2] [1 3])))))
    (testing "Weight"
      (are [expected got] (= expected got)
        77 (weight g1 1 2)
        77 (weight g6 2 1)))))
