(ns meal-planner.core
  (:require [meal-planner.csv :as csv]
            [clojure.string :as str]
            [clojure.edn :as edn])
  (:import [java.time LocalDateTime]))

;; wants
;; user can send text to regenerate options entirely
;; user can 2x the recipe ingredients

(def recipes
  (csv/read-csv "resources/recipes.csv"))

(def grocery-items
  (csv/read-csv "resources/grocery_items.csv"))

(def state-file "resources/state.edn")

(defn now []
  (str (LocalDateTime/now)))

(defn save-state! [state]
  (spit state-file (pr-str state)))

(defn load-state []
  (edn/read-string (slurp state-file)))

(defn suggest-five []
  (take 5 (shuffle recipes)))

(defn ingredients-for [meals]
  (let [meal-set (set meals)]
    (filter #(meal-set (:meal %))
            grocery-items)))

(defn quantity-number [item]
  (Double/parseDouble (:quantity item)))

(defn combine-ingredients [items]
  (->> items
       (group-by (juxt :ingredient :unit))
       (map (fn [[[ingredient unit] items]]
              (assoc (first items)
                     :quantity
                     (reduce +
                             (map quantity-number items)))))
       vec))

(defn shopping-list [meals]
  (map #(select-keys % [:ingredient :quantity :unit :category :aisle])
       (ingredients-for meals)))

(defn shopping-list-by-aisle [meals]
  (group-by :aisle
            (combine-ingredients
             (ingredients-for meals))))

(defn format-quantity [q]
  (if (== q (Math/floor q))
    (str (int q))
    (str q)))

(defn format-item [item]
  (str "☐ "
       (:ingredient item)
       " ("
       (format-quantity (:quantity item))
       " "
       (:unit item)
       ")"))

(defn format-meals [meals]
  (str "Meals\n\n"
       (clojure.string/join
        "\n"
        (map #(str "☑ " %) meals))
       "\n\n"))

(defn format-aisle [[aisle items]]
  (str
   "\n"
   (clojure.string/upper-case aisle)
   "\n"
   (clojure.string/join
    "\n"
    (map format-item items))))

(def aisle-order
  ["Produce"
   "Vegetables"
   "Fruit"
   "Meat"
   "Seafood"
   "Dairy"
   "Refrigerated"
   "Pasta"
   "Grains"
   "Canned"
   "Sauces"
   "Tortillas"])

(defn sort-aisles [grouped]
  (sort-by
   #(or (.indexOf aisle-order (first %))
        999)
   grouped))

(defn grocery-note [meals]
  (str
   (format-meals meals)
   "Grocery List\n\n"
   (apply str
          (map format-aisle
               (sort-aisles
                (shopping-list-by-aisle meals))))))

(defn numbered-options [recipes]
  (map-indexed
   (fn [idx recipe]
     (assoc recipe :id (inc idx)))
   recipes))

(defn parse-selection [response]
  (map #(Integer/parseInt %)
       (str/split response #"[,\s]+")))

(defn select-meals [options selections]
  (filter #(contains? (set selections) (:id %))
          options))

(def current-options (atom nil))

(defn generate-options []
  (let [options (numbered-options (suggest-five))
        state {:created-at (now)
               :options options}]
    (save-state! state)
    options))

(defn plan-week [response]
  (let [options (:options (load-state))
        selections (parse-selection response)]
    (select-meals options selections)))

(defn meal-prompt []
  (let [options (:options (load-state))]
    (str
     "Pick 3 meals:\n\n"
     (clojure.string/join
      "\n"
      (map #(str (:id %) ". " (:meal %))
           options))
     "\n\nReply with numbers, e.g. 1,3,5")))

(defn groceries-for-response [response]
  (->> (plan-week response)
       (map :meal)
       ingredients-for
       combine-ingredients))
