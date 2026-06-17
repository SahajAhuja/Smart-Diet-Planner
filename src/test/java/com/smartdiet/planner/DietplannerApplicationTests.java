package com.smartdiet.planner;

import com.smartdiet.planner.dto.UserDTO;
import com.smartdiet.planner.model.User;
import com.smartdiet.planner.repository.UserRepository;
import com.smartdiet.planner.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class DietplannerApplicationTests {

	@Autowired
	private UserService userService;

	@MockBean
	private UserRepository userRepository;

	private User sampleUser;

	@BeforeEach
	void setUp() {
		sampleUser = User.builder()
				.userId("test-user-id")
				.name("Alice")
				.email("alice@example.com")
				.age(30)
				.gender("Female")
				.height(160.0) // 1.6 meters
				.weight(64.0)
				.goal("Weight Loss")
				.build();
	}

	@Test
	void contextLoads() {
		assertNotNull(userService);
	}

	@Test
	void testCalculateBMI() {
		when(userRepository.findById("test-user-id")).thenReturn(Optional.of(sampleUser));

		Map<String, Object> bmiData = userService.calculateBMI("test-user-id");

		assertNotNull(bmiData);
		assertEquals("test-user-id", bmiData.get("userId"));
		assertEquals("Alice", bmiData.get("userName"));
		// BMI = 64 / (1.6 * 1.6) = 64 / 2.56 = 25.0
		assertEquals(25.0, bmiData.get("bmi"));
		assertEquals("Overweight", bmiData.get("category")); // 25.0 is overweight boundary

		verify(userRepository, times(1)).findById("test-user-id");
	}

	@Test
	void testCalculateDailyCaloriesFemaleWeightLoss() {
		when(userRepository.findById("test-user-id")).thenReturn(Optional.of(sampleUser));

		// BMR (Female) = (10 * 64) + (6.25 * 160) - (5 * 30) - 161 = 640 + 1000 - 150 - 161 = 1329.0
		// TDEE (Moderately Active) = 1329 * 1.55 = 2059.95
		// Goal (Weight Loss) = TDEE - 500 = 1559.95 (rounded to 1559.95)
		Map<String, Object> calorieData = userService.calculateDailyCalories("test-user-id", "Moderately Active");

		assertNotNull(calorieData);
		assertEquals("test-user-id", calorieData.get("userId"));
		assertEquals(1329.0, calorieData.get("bmr"));
		assertEquals(2059.95, calorieData.get("tdee"));
		assertEquals(1559.95, calorieData.get("calorieTarget"));

		verify(userRepository, times(1)).findById("test-user-id");
	}

	@Test
	void testCalculateDailyCaloriesMaleMaintenance() {
		User maleUser = User.builder()
				.userId("test-male-id")
				.name("Bob")
				.email("bob@example.com")
				.age(25)
				.gender("Male")
				.height(180.0)
				.weight(80.0)
				.goal("Maintenance")
				.build();

		when(userRepository.findById("test-male-id")).thenReturn(Optional.of(maleUser));

		// BMR (Male) = (10 * 80) + (6.25 * 180) - (5 * 25) + 5 = 800 + 1125 - 125 + 5 = 1805.0
		// TDEE (Sedentary) = 1805 * 1.2 = 2166.0
		// Goal (Maintenance) = 2166.0
		Map<String, Object> calorieData = userService.calculateDailyCalories("test-male-id", "Sedentary");

		assertNotNull(calorieData);
		assertEquals("test-male-id", calorieData.get("userId"));
		assertEquals(1805.0, calorieData.get("bmr"));
		assertEquals(2166.0, calorieData.get("tdee"));
		assertEquals(2166.0, calorieData.get("calorieTarget"));

		verify(userRepository, times(1)).findById("test-male-id");
	}
}
