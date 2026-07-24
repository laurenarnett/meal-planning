# Meal Planner

A Clojure-based meal planning assistant that runs on a Raspberry Pi, suggests meals, generates a grocery list, and sends the ingredients to a shared AnyList grocery list.

## Overview

The meal planner helps automate weekly meal planning:

1. Generate a list of meal suggestions
2. Select meals for the week
3. Combine ingredients across recipes
4. Send the grocery list to AnyList

Current workflow:

```
./meal-planner
      |
      v
Generate meal suggestions
      |
      v
Select meals
      |
      v
Combine ingredients
      |
      v
Send groceries to AnyList
```

## Requirements

* Raspberry Pi (or any Linux/macOS machine)
* Java (required for Clojure)
* Clojure CLI tools
* Node.js (required for AnyList integration)
* An AnyList account

## Project Structure

```
meal-planning/
├── resources/
│   ├── recipes.csv
│   ├── grocery_items.csv
│   └── state.edn
│
├── src/
│   └── meal_planner/
│       ├── core.clj
│       ├── main.clj
│       ├── csv.clj
│       └── anylist.clj
│
├── scripts/
│   ├── add-to-anylist.js
│   ├── package.json
│   └── package-lock.json
│
├── deps.edn
└── meal-planner
```

## Setup

### Install Java

Clojure requires Java.

Verify:

```bash
java --version
```

### Install Clojure

Verify:

```bash
clojure -Sdescribe
```

### Install Node dependencies

From the scripts directory:

```bash
cd scripts
npm install
```

## AnyList Setup

The project uses the reverse-engineered AnyList API library:

```
anylist
```

The Node script authenticates with AnyList and adds grocery items to a shared list.

Create:

```
scripts/.env
```

with:

```env
ANYLIST_EMAIL=your-email@example.com
ANYLIST_PASSWORD=your-password
```

Do not commit this file.

## Running the Meal Planner

From the project root:

```bash
./meal-planner
```

Example:

```
Pick 3 meals:

1. garlic shrimp
2. baked salmon
3. silky sicilian pasta
4. shakshuka
5. enchilada

Reply with numbers, e.g. 1,3,5
```

Select meals:

```
1,3,5
```

The planner will:

* Find the selected recipes
* Combine duplicate ingredients
* Send the ingredients to AnyList

Example:

```
Adding to AnyList...

Added bell pepper
Added chicken
Added shrimp
Added shredded cheese

Done!
```

## Recipe Data

Recipes are stored in:

```
resources/recipes.csv
```

Example:

```csv
meal,protein,carb,vegetable
chicken tikka,chicken,rice,bell pepper
```

Ingredients are stored in:

```
resources/grocery_items.csv
```

Example:

```csv
meal,ingredient,quantity,unit,category,aisle
chicken tikka,chicken thighs,1.5,lb,Meat,Meat
```

## State Management

The planner stores generated meal options in:

```
resources/state.edn
```

This allows the selected meal numbers to correspond to the same suggestions shown in the prompt.

Example:

```clojure
{:created-at "2026-07-24T12:00"
 :options
 [{:id 1
   :meal "chicken tikka"}]}
```

## Development

Start a Clojure REPL:

```bash
clojure
```

Load the project:

```clojure
(require '[meal-planner.core :as core])
```

Generate meal options:

```clojure
(core/generate-options)
```

Preview the prompt:

```clojure
(core/meal-prompt)
```

Test grocery generation:

```clojure
(core/groceries-for-response "1,3,5")
```

## Future Ideas

Potential future improvements:

* Send meal suggestions by SMS
* Reply with meal selections by text message
* Avoid recently cooked meals
* Automatically create Whole Foods orders
* Support doubling recipes
* Schedule weekly meal planning reminders
* Pull new recipes from Google Sheet

## Security Notes

Never commit:

```
.env
scripts/.env
```

The AnyList credentials should remain local to the Raspberry Pi.
