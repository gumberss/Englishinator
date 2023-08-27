(ns englishinator.core
  (:require [clojure.java.io :as io]
            [clojure.data.json :as json]))

(def expressions-file (io/resource "expressions.json"))

(defn select-expressions
  [expressions-group]
  (let [categories (map-indexed #(hash-map :index %1 :category %2) (keys expressions-group))]
    (println "Chose a category")
    (doseq [c categories]
      (println (str (:index c) " - " (:category c))))
    (let [category-index (read-line)
          category-selected (->> categories
                                 (filter #(= category-index (str (:index %))))
                                 first)
          expressions-selected (get expressions-group (:category category-selected))]
      (shuffle expressions-selected))))


(defn in-game
  [continue-game-atom selected-expressions]
  (let [selected-expression-atom (atom selected-expressions)]
    (while @continue-game-atom
      (let [{:keys [expression meanings]} (peek @selected-expression-atom)
            _ (swap! selected-expression-atom pop)
            {:keys [meaning example translation]} (first meanings)]
        (println "Expression: " expression)
        (println "Digit anything to check the meaning")
        (read-line)
        (println)
        (println "Meaning: " meaning)
        (println "Example: " example)
        (println "Translation: " translation)
        (println)
        (println "Write change to change the category")
        (println "Write exit to exit the game")
        (println "Digit 1 if you know the expression")
        (println "Digit 2 if you don't know the expression")
        (println)
        (let [user-command (read-line)]
          (cond (= "exit" user-command) (swap! continue-game-atom (constantly false))))))))

(defn start
  []
  (let [expressions (-> (slurp expressions-file)
                        (json/read-str :key-fn keyword))
        expressions-group (group-by :type expressions)
        selected-expressions (select-expressions expressions-group)
        continue-game (atom true)]


    (in-game continue-game selected-expressions)))

