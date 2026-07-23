(ns meal-planner.csv
  (:require [clojure.data.csv :as data-csv]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(defn header->keyword [header]
  (-> header
      str/lower-case
      (str/replace " " "-")
      keyword))

(defn read-csv [filename]
  (with-open [reader (io/reader filename)]
    (let [[headers & rows] (data-csv/read-csv reader)
          headers (map header->keyword headers)]
      (doall
       (map #(zipmap headers %) rows)))))
