(ns meal-planner.anylist
  (:require [clojure.java.shell :refer [sh]]
            [cheshire.core :as json]))

(defn send-to-anylist [ingredients]
  (let [result
        (sh "node"
            "scripts/add-to-anylist.js"
            :in (json/generate-string ingredients))]
    (println (:out result))
    (when-not (zero? (:exit result))
      (throw (ex-info "AnyList failed" result)))))
