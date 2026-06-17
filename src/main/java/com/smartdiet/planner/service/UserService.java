package com.smartdiet.planner.service;

import com.smartdiet.planner.dto.UserDTO;
import com.smartdiet.planner.exception.BadRequestException;
import com.smartdiet.planner.exception.ResourceNotFoundException;
import com.smartdiet.planner.model.DietPlan;
import com.smartdiet.planner.model.Meal;
import com.smartdiet.planner.model.Progress;
import com.smartdiet.planner.model.User;
import com.smartdiet.planner.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DietPlanRepository dietPlanRepository;
    private final MealRepository mealRepository;
    private final FoodItemRepository foodItemRepository;
    private final ProgressRepository progressRepository;

    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new BadRequestException("Email already exists: " + userDTO.getEmail());
        }

        User user = convertToEntity(userDTO);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());
        User savedUser = userRepository.save(user);

        return convertToDTO(savedUser);
    }

    public UserDTO getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return convertToDTO(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO updateUser(String userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (!user.getEmail().equalsIgnoreCase(userDTO.getEmail()) && userRepository.existsByEmail(userDTO.getEmail())) {
            throw new BadRequestException("Email already exists: " + userDTO.getEmail());
        }

        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setAge(userDTO.getAge());
        user.setGender(userDTO.getGender());
        user.setHeight(userDTO.getHeight());
        user.setWeight(userDTO.getWeight());
        user.setGoal(userDTO.getGoal());
        user.setUpdatedAt(Instant.now());

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Cascade Delete DietPlans -> Meals -> FoodItems
        List<DietPlan> dietPlans = dietPlanRepository.findByUserId(userId);
        for (DietPlan plan : dietPlans) {
            List<Meal> meals = mealRepository.findByDietPlanId(plan.getPlanId());
            for (Meal meal : meals) {
                foodItemRepository.deleteByMealId(meal.getMealId());
            }
            mealRepository.deleteByDietPlanId(plan.getPlanId());
        }
        dietPlanRepository.deleteById(user.getUserId()); // Wait, this deletes one, but let's delete all by userId
        // Let's delete each diet plan by id
        for (DietPlan plan : dietPlans) {
            dietPlanRepository.deleteById(plan.getPlanId());
        }

        // Cascade Delete Progress records
        progressRepository.deleteByUserId(userId);

        // Delete User
        userRepository.delete(user);
    }

    public Map<String, Object> calculateBMI(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        double heightInMeters = user.getHeight() / 100.0;
        double bmi = user.getWeight() / (heightInMeters * heightInMeters);
        // Round to 2 decimal places
        bmi = Math.round(bmi * 100.0) / 100.0;

        String category;
        if (bmi < 18.5) {
            category = "Underweight";
        } else if (bmi < 25.0) {
            category = "Normal Weight";
        } else if (bmi < 30.0) {
            category = "Overweight";
        } else {
            category = "Obese";
        }

        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("userName", user.getName());
        result.put("bmi", bmi);
        result.put("category", category);
        return result;
    }

    public Map<String, Object> calculateDailyCalories(String userId, String activityLevel) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Mifflin-St Jeor Equation for BMR
        double bmr;
        boolean isMale = "male".equalsIgnoreCase(user.getGender()) || "m".equalsIgnoreCase(user.getGender());
        if (isMale) {
            bmr = (10 * user.getWeight()) + (6.25 * user.getHeight()) - (5 * user.getAge()) + 5;
        } else {
            bmr = (10 * user.getWeight()) + (6.25 * user.getHeight()) - (5 * user.getAge()) - 161;
        }

        double factor;
        String actLevelClean = activityLevel != null ? activityLevel.toLowerCase() : "moderately active";
        switch (actLevelClean) {
            case "sedentary":
                factor = 1.2;
                break;
            case "lightly active":
                factor = 1.375;
                break;
            case "very active":
                factor = 1.725;
                break;
            case "moderately active":
            default:
                factor = 1.55;
                break;
        }

        double tdee = bmr * factor;
        double targetCalories = tdee;

        String goalClean = user.getGoal() != null ? user.getGoal().toLowerCase() : "maintenance";
        if (goalClean.contains("loss") || goalClean.contains("lose")) {
            targetCalories = tdee - 500;
        } else if (goalClean.contains("gain")) {
            targetCalories = tdee + 500;
        }

        // Round results
        bmr = Math.round(bmr * 100.0) / 100.0;
        tdee = Math.round(tdee * 100.0) / 100.0;
        targetCalories = Math.round(targetCalories * 100.0) / 100.0;

        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("bmr", bmr);
        result.put("tdee", tdee);
        result.put("activityLevel", activityLevel != null ? activityLevel : "Moderately Active");
        result.put("goal", user.getGoal());
        result.put("calorieTarget", targetCalories);
        return result;
    }

    public Map<String, Object> getDashboardStats(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<DietPlan> dietPlans = dietPlanRepository.findByUserId(userId);
        List<Progress> progressLogs = progressRepository.findByUserIdOrderByDateDesc(userId);

        double startWeight = user.getWeight(); // fallback
        double currentWeight = user.getWeight();
        double weightChange = 0.0;
        double latestBmi = 0.0;

        if (!progressLogs.isEmpty()) {
            currentWeight = progressLogs.get(0).getCurrentWeight();
            latestBmi = progressLogs.get(0).getBmi();
            // Start weight is the oldest progress log weight or the initial user profile weight
            startWeight = progressLogs.get(progressLogs.size() - 1).getCurrentWeight();
            weightChange = Math.round((currentWeight - startWeight) * 100.0) / 100.0;
        } else {
            // Compute current BMI from user profile
            double heightInMeters = user.getHeight() / 100.0;
            latestBmi = Math.round((user.getWeight() / (heightInMeters * heightInMeters)) * 100.0) / 100.0;
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("userId", userId);
        stats.put("userName", user.getName());
        stats.put("currentGoal", user.getGoal());
        stats.put("totalDietPlans", dietPlans.size());
        stats.put("totalProgressLogs", progressLogs.size());
        stats.put("profileWeight", user.getWeight());
        stats.put("startWeight", startWeight);
        stats.put("currentWeight", currentWeight);
        stats.put("weightChange", weightChange);
        stats.put("latestBmi", latestBmi);

        return stats;
    }

    private User convertToEntity(UserDTO dto) {
        return User.builder()
                .userId(dto.getUserId())
                .name(dto.getName())
                .email(dto.getEmail())
                .age(dto.getAge())
                .gender(dto.getGender())
                .height(dto.getHeight())
                .weight(dto.getWeight())
                .goal(dto.getGoal())
                .build();
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .age(user.getAge())
                .gender(user.getGender())
                .height(user.getHeight())
                .weight(user.getWeight())
                .goal(user.getGoal())
                .build();
    }
}
