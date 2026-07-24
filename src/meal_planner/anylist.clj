(ns meal-planner.anylist
  (:require [clojure.java.shell :refer [sh]]
            [cheshire.core :as json]))

(defn send-to-anylist [ingredients]
  (sh "node"
      "scripts/add-to-anylist.js"
      :in (json/generate-string ingredients)))
