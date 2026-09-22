package kr.co.motive.admin.exercise.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.motive.admin.exercise.service.AdminExerciseService;
import kr.co.motive.common.response.ApiResponse;
import kr.co.motive.common.util.PageResponseDto;
import kr.co.motive.exercise.dto.ExerciseInsertDto;
import kr.co.motive.exercise.dto.ExerciseResponseDto;
import kr.co.motive.exercise.dto.ExerciseUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "관리자 운동 Controller", description = "관리자 운동 마스터 컨트롤러")
public class AdminExerciseController {

	private final AdminExerciseService adminExerciseService;

	@Operation(summary = "운동 목록 조회", description = "관리자용 운동 목록을 조회한다")
	@GetMapping("/admin/exercises")
	public ApiResponse<PageResponseDto<ExerciseResponseDto>> getExerciseList(
			@RequestParam(required = false) String bodyPartCd,
			@RequestParam(required = false) String equipmentCd,
			@RequestParam(required = false) String name,
			@RequestParam(defaultValue = "1") int pageNum,
			@RequestParam(defaultValue = "15") int pageSize) {

		return ApiResponse.ok(adminExerciseService.getExerciseList(bodyPartCd, equipmentCd, name, pageNum, pageSize));
	}

	@Operation(summary = "운동 상세 조회", description = "관리자용 운동 상세를 조회한다")
	@GetMapping("/admin/exercises/{exerciseId}")
	public ApiResponse<ExerciseResponseDto> getExercise(@PathVariable Long exerciseId) {

		return ApiResponse.ok(adminExerciseService.getExercise(exerciseId));
	}

	@Operation(summary = "운동 등록", description = "운동을 등록한다")
	@PostMapping("/admin/exercises")
	public ApiResponse<Void> insertExercise(@Valid @RequestBody ExerciseInsertDto dto) {

		adminExerciseService.insertExercise(dto);
		return ApiResponse.ok();
	}

	@Operation(summary = "운동 수정", description = "운동을 수정한다")
	@PutMapping("/admin/exercises/{exerciseId}")
	public ApiResponse<Void> updateExercise(@PathVariable Long exerciseId, @Valid @RequestBody ExerciseUpdateDto dto) {

		adminExerciseService.updateExercise(exerciseId, dto);
		return ApiResponse.ok();
	}

	@Operation(summary = "운동 삭제", description = "운동을 삭제한다")
	@DeleteMapping("/admin/exercises/{exerciseId}")
	public ApiResponse<Void> deleteExercise(@PathVariable Long exerciseId) {

		adminExerciseService.deleteExercise(exerciseId);
		return ApiResponse.ok();
	}

}
