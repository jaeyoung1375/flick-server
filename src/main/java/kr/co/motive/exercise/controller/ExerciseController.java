package kr.co.motive.exercise.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.motive.common.response.ApiResponse;
import kr.co.motive.exercise.dto.ExerciseResponseDto;
import kr.co.motive.exercise.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "운동 Controller", description = "운동 마스터 컨트롤러")
public class ExerciseController {

	private final ExerciseService exerciseService;

	@Operation(summary = "운동 목록 조회", description = "운동 목록을 조회한다")
	@GetMapping("/exercises")
	public ApiResponse<List<ExerciseResponseDto>> getExerciseList() {

		return ApiResponse.ok(exerciseService.getExerciseList());
	}

	@Operation(summary = "운동 상세 조회", description = "운동 상세를 조회한다")
	@GetMapping("/exercises/{exerciseId}")
	public ApiResponse<ExerciseResponseDto> getExercise(@PathVariable Long exerciseId) {

		return ApiResponse.ok(exerciseService.getExercise(exerciseId));
	}

}