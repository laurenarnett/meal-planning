(ns meal-planner.main
  (:require [meal-planner.core :as core]
            [meal-planner.anylist :as anylist])
  (:gen-class))

(defn -main []
  (core/generate-options)

  (println (core/meal-prompt))

  (let [response (read-line)
        combined (core/groceries-for-response response)]

    (println "\nAdding to AnyList...")
    (anylist/send-to-anylist combined)

    (println "Done!")))
