# Smart Diet Planner API

A backend-based RESTful API service developed using **Spring Boot**, **MongoDB**, **Swagger/OpenAPI**, and **Postman**. 
The system manages Users, Diet Plans, Meals, Food Items, and Progress Tracking, modeling realistic one-to-many relationships in a Document-based database.

---

## 🚀 Features

### 1. Core CRUD Management (5 Entities)
* **Users (`/api/users`)**: Create, update, get, list, and delete users (with cascade deletion of user diet plans and progress logs).
* **Diet Plans (`/api/diet-plans`)**: Create, update, get, and list plans by user. Contains calorie targets and macro details.
* **Meals (`/api/meals`)**: Add/remove meals (Breakfast, Lunch, Dinner, Snacks) linked to specific diet plans.
* **Food Items (`/api/foods`)**: Create and associate food items inside specific meals. Parent meal total calories are automatically recalculated.
* **Progress Tracking (`/api/progress`)**: Log progress data (weight, date, auto-calculated BMI) over time.

### 2. Advanced Health Engines & Calculators
* **BMI Calculator API**: Exposes `/api/users/{id}/bmi` to calculate current Body Mass Index and map users to health categories (Underweight, Normal, Overweight, Obese).
* **Daily Calorie Target Calculator API**: Exposes `/api/users/{id}/calorie-calculator` calculating Basal Metabolic Rate (BMR) using the Mifflin-St Jeor equation, Total Daily Energy Expenditure (TDEE) based on activity levels (Sedentary, Lightly Active, Moderately Active, Very Active), and target calorie output adjusted for user goals (Weight Loss, Weight Gain, Maintenance).
* **Recommended Diet Generator API**: Exposes `/api/diet-plans/user/{userId}/recommend` to automatically generate a complete diet plan customized to the user's BMR, including target calorie and macronutrient breakdowns (Protein, Carbs, Fats) and pre-populating breakfast, lunch, dinner, and snack meals with matching food items.
* **Weight Tracking API**: Exposes `/api/progress/user/{userId}/track-weight` to update the user's current profile weight and automatically log a new progress record.
* **Dashboard Statistics API**: Exposes `/api/users/{id}/dashboard-stats` returning plan counts, total logs, starting weight, current weight, total weight change, and BMI trends.

---

## 🛠️ Tech Stack
* **Language**: Java 17
* **Framework**: Spring Boot 3.3.0
* **Database**: MongoDB (Spring Data MongoDB)
* **API Spec & UI**: Swagger 3 / SpringDoc OpenAPI 2.5.0
* **Build Tool**: Maven
* **Utilities**: Lombok, Spring Validation

---

## 📁 Project Structure

```text
smart-diet-planner/
├── mongo/
│   └── db-setup.js             # MongoDB shell seeding script
├── postman/
│   └── smart-diet-planner.json # Postman API collection
├── src/
│   ├── main/
│   │   ├── java/com/smartdiet/planner/
│   │   │   ├── config/         # Swagger Config
│   │   │   ├── controller/     # Controllers
│   │   │   ├── dto/            # Data Transfer Objects
│   │   │   ├── exception/      # Exception wrappers & handlers
│   │   │   ├── model/          # Entities
│   │   │   ├── repository/     # MongoRepositories
│   │   │   ├── response/       # Generic API wrapper (ApiResponse)
│   │   │   ├── service/        # Business Logic Services
│   │   │   └── DietplannerApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/smartdiet/planner/
│           └── DietplannerApplicationTests.java
├── pom.xml
└── README.md
```

---

## 💻 Setup & Execution

### 1. Pre-requisites
Make sure you have JDK 17 (or newer) and MongoDB installed and running on default port `27017`.

### 2. Set Up Database and Seed Sample Data
Execute the setup script using `mongosh` or `mongo` shell to initialize database collections with realistic test data:
```bash
mongosh localhost:27017/smart_diet_planner mongo/db-setup.js
```

### 3. Run the Backend Server
Set your `JAVA_HOME` if needed and run the Spring Boot application using Maven:
```bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-26.jdk/Contents/Home
mvn spring-boot:run
```

---

## 📑 API Verification & Testing

### 1. Interactive Swagger UI
Open your browser and navigate to:
[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
Use the web page interface to directly test endpoints and check parameters.

### 2. Postman Collection
1. Open Postman.
2. Click **Import** and select the [postman/smart-diet-planner.json](file:///Users/aadityasangwan/Desktop/SMART%20DIET%20PLANNER/postman/smart-diet-planner.json) file.
3. Once imported, define environment variables or collection variables (such as `userId`, `planId`, `mealId`, `foodId`) to start executing and validating the requests sequentially.

### 3. Run JUnit Integration Tests
Verify math formulas, context loads, and mappings by running tests:
```bash
mvn test
```
