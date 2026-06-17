package com.smartdiet.planner.service;

import com.smartdiet.planner.dto.ProgressDTO;
import com.smartdiet.planner.exception.ResourceNotFoundException;
import com.smartdiet.planner.model.Progress;
import com.smartdiet.planner.model.User;
import com.smartdiet.planner.repository.ProgressRepository;
import com.smartdiet.planner.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final UserRepository userRepository;

    public ProgressDTO createProgress(ProgressDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));

        double heightInMeters = user.getHeight() / 100.0;
        double bmi = dto.getCurrentWeight() / (heightInMeters * heightInMeters);
        bmi = Math.round(bmi * 100.0) / 100.0;

        Progress progress = convertToEntity(dto);
        progress.setBmi(bmi);
        if (progress.getDate() == null) {
            progress.setDate(LocalDate.now());
        }

        Progress saved = progressRepository.save(progress);
        return convertToDTO(saved);
    }

    public ProgressDTO getProgressById(String progressId) {
        Progress progress = progressRepository.findById(progressId)
                .orElseThrow(() -> new ResourceNotFoundException("Progress record not found with id: " + progressId));
        return convertToDTO(progress);
    }

    public List<ProgressDTO> getProgressByUserId(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return progressRepository.findByUserIdOrderByDateDesc(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void deleteProgress(String progressId) {
        Progress progress = progressRepository.findById(progressId)
                .orElseThrow(() -> new ResourceNotFoundException("Progress record not found with id: " + progressId));
        progressRepository.delete(progress);
    }

    public ProgressDTO trackWeight(String userId, double currentWeight) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Update User profile weight
        user.setWeight(currentWeight);
        userRepository.save(user);

        // Calculate BMI
        double heightInMeters = user.getHeight() / 100.0;
        double bmi = currentWeight / (heightInMeters * heightInMeters);
        bmi = Math.round(bmi * 100.0) / 100.0;

        // Create new progress record
        Progress progress = Progress.builder()
                .userId(userId)
                .currentWeight(currentWeight)
                .bmi(bmi)
                .date(LocalDate.now())
                .build();

        Progress saved = progressRepository.save(progress);
        return convertToDTO(saved);
    }

    private Progress convertToEntity(ProgressDTO dto) {
        return Progress.builder()
                .progressId(dto.getProgressId())
                .userId(dto.getUserId())
                .currentWeight(dto.getCurrentWeight())
                .bmi(dto.getBmi())
                .date(dto.getDate())
                .build();
    }

    private ProgressDTO convertToDTO(Progress progress) {
        return ProgressDTO.builder()
                .progressId(progress.getProgressId())
                .userId(progress.getUserId())
                .currentWeight(progress.getCurrentWeight())
                .bmi(progress.getBmi())
                .date(progress.getDate())
                .build();
    }
}
