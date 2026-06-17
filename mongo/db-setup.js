// MongoDB Shell Database Setup and Seeding Script
// To execute: mongo localhost:27017/smart_diet_planner db-setup.js OR mongosh localhost:27017/smart_diet_planner db-setup.js

db = db.getSiblingDB("smart_diet_planner");

print("--- Starting database initialization and seeding for Smart Diet Planner ---");

// Drop existing collections to start fresh
db.users.drop();
db.diet_plans.drop();
db.meals.drop();
db.food_items.drop();
db.progress.drop();

print("Cleared existing collections.");

// Seed Users
var userIds = [
  "usr_000000000000000000000001",
  "usr_000000000000000000000002"
];

db.users.insertMany([
  {
    "_id": userIds[0],
    "name": "Jane Doe",
    "email": "jane.doe@example.com",
    "age": 29,
    "gender": "Female",
    "height": 165.0,
    "weight": 68.0,
    "goal": "Weight Loss",
    "createdAt": new Date(),
    "updatedAt": new Date(),
    "_class": "com.smartdiet.planner.model.User"
  },
  {
    "_id": userIds[1],
    "name": "Alex Smith",
    "email": "alex.smith@example.com",
    "age": 34,
    "gender": "Male",
    "height": 182.0,
    "weight": 75.0,
    "goal": "Maintenance",
    "createdAt": new Date(),
    "updatedAt": new Date(),
    "_class": "com.smartdiet.planner.model.User"
  }
]);
print("Seeded 2 Users.");

// Seed Diet Plans
var planIds = [
  "pln_000000000000000000000001",
  "pln_000000000000000000000002"
];

db.diet_plans.insertMany([
  {
    "_id": planIds[0],
    "planName": "Low Carb Fat Loss",
    "calorieTarget": 1600.0,
    "proteinTarget": 120.0,
    "carbsTarget": 110.0,
    "fatsTarget": 53.0,
    "userId": userIds[0],
    "createdAt": new Date(),
    "updatedAt": new Date(),
    "_class": "com.smartdiet.planner.model.DietPlan"
  },
  {
    "_id": planIds[1],
    "planName": "Lean Muscle & Maintenance",
    "calorieTarget": 2400.0,
    "proteinTarget": 165.0,
    "carbsTarget": 270.0,
    "fatsTarget": 73.0,
    "userId": userIds[1],
    "createdAt": new Date(),
    "updatedAt": new Date(),
    "_class": "com.smartdiet.planner.model.DietPlan"
  }
]);
print("Seeded 2 Diet Plans.");

// Seed Meals
var mealIds = [
  "mel_000000000000000000000001",
  "mel_000000000000000000000002",
  "mel_000000000000000000000003",
  "mel_000000000000000000000004"
];

db.meals.insertMany([
  {
    "_id": mealIds[0],
    "mealType": "Breakfast",
    "totalCalories": 420.0,
    "dietPlanId": planIds[0],
    "_class": "com.smartdiet.planner.model.Meal"
  },
  {
    "_id": mealIds[1],
    "mealType": "Lunch",
    "totalCalories": 580.0,
    "dietPlanId": planIds[0],
    "_class": "com.smartdiet.planner.model.Meal"
  },
  {
    "_id": mealIds[2],
    "mealType": "Breakfast",
    "totalCalories": 650.0,
    "dietPlanId": planIds[1],
    "_class": "com.smartdiet.planner.model.Meal"
  },
  {
    "_id": mealIds[3],
    "mealType": "Dinner",
    "totalCalories": 850.0,
    "dietPlanId": planIds[1],
    "_class": "com.smartdiet.planner.model.Meal"
  }
]);
print("Seeded 4 Meals.");

// Seed Food Items
db.food_items.insertMany([
  {
    "foodName": "Boiled Eggs",
    "calories": 155.0,
    "protein": 13.0,
    "carbs": 1.1,
    "fats": 11.0,
    "quantity": 2.0,
    "mealId": mealIds[0],
    "_class": "com.smartdiet.planner.model.FoodItem"
  },
  {
    "foodName": "Avocado",
    "calories": 160.0,
    "protein": 2.0,
    "carbs": 8.5,
    "fats": 14.7,
    "quantity": 1.0,
    "mealId": mealIds[0],
    "_class": "com.smartdiet.planner.model.FoodItem"
  },
  {
    "foodName": "Grilled Chicken Breast",
    "calories": 165.0,
    "protein": 31.0,
    "carbs": 0.0,
    "fats": 3.6,
    "quantity": 2.0,
    "mealId": mealIds[1],
    "_class": "com.smartdiet.planner.model.FoodItem"
  },
  {
    "foodName": "Brown Rice",
    "calories": 111.0,
    "protein": 2.6,
    "carbs": 23.0,
    "fats": 0.9,
    "quantity": 1.5,
    "mealId": mealIds[1],
    "_class": "com.smartdiet.planner.model.FoodItem"
  },
  {
    "foodName": "Oatmeal with Almonds",
    "calories": 380.0,
    "protein": 13.0,
    "carbs": 55.0,
    "fats": 12.0,
    "quantity": 1.5,
    "mealId": mealIds[2],
    "_class": "com.smartdiet.planner.model.FoodItem"
  },
  {
    "foodName": "Whole Milk",
    "calories": 150.0,
    "protein": 8.0,
    "carbs": 12.0,
    "fats": 8.0,
    "quantity": 1.0,
    "mealId": mealIds[2],
    "_class": "com.smartdiet.planner.model.FoodItem"
  },
  {
    "foodName": "Baked Salmon Fillet",
    "calories": 206.0,
    "protein": 22.0,
    "carbs": 0.0,
    "fats": 12.0,
    "quantity": 2.0,
    "mealId": mealIds[3],
    "_class": "com.smartdiet.planner.model.FoodItem"
  },
  {
    "foodName": "Sweet Potato",
    "calories": 86.0,
    "protein": 1.6,
    "carbs": 20.0,
    "fats": 0.1,
    "quantity": 2.5,
    "mealId": mealIds[3],
    "_class": "com.smartdiet.planner.model.FoodItem"
  }
]);
print("Seeded 8 Food Items.");

// Seed Progress records
db.progress.insertMany([
  {
    "userId": userIds[0],
    "currentWeight": 68.0,
    "bmi": 24.98,
    "date": ISODate("2026-06-01T00:00:00Z"),
    "_class": "com.smartdiet.planner.model.Progress"
  },
  {
    "userId": userIds[0],
    "currentWeight": 67.2,
    "bmi": 24.68,
    "date": ISODate("2026-06-08T00:00:00Z"),
    "_class": "com.smartdiet.planner.model.Progress"
  },
  {
    "userId": userIds[0],
    "currentWeight": 66.5,
    "bmi": 24.43,
    "date": ISODate("2026-06-15T00:00:00Z"),
    "_class": "com.smartdiet.planner.model.Progress"
  },
  {
    "userId": userIds[1],
    "currentWeight": 75.0,
    "bmi": 22.64,
    "date": ISODate("2026-06-15T00:00:00Z"),
    "_class": "com.smartdiet.planner.model.Progress"
  }
]);
print("Seeded 4 Progress records.");

print("--- Database setup and seeding completed successfully! ---");
