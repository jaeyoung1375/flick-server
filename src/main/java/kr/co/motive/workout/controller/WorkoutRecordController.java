package kr.co.motive.workout.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.motive.common.response.ApiResponse;
import kr.co.motive.common.util.SecurityUtil;
import kr.co.motive.workout.dto.WorkoutRecordInsertDto;
import kr.co.motive.workout.dto.WorkoutRecordResponseDto;
import kr.co.motive.workout.dto.WorkoutRecordUpdateDto;
import kr.co.motive.workout.service.WorkoutRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "운동기록 Controller", description = "회원 운동기록 컨트롤러")
public class WorkoutRecordController {

	private final WorkoutRecordService workoutRecordService;

	@Operation(summary = "내 운동기록 목록 조회", description = "로그인한 회원의 운동기록 목록을 조회한다 (운동/세트 상세는 포함하지 않음)")
	@GetMapping("/workout-records")
	public ApiResponse<List<WorkoutRecordResponseDto>> getWorkoutRecordList() {

		return ApiResponse.ok(workoutRecordService.getWorkoutRecordList(SecurityUtil.getUserId()));
	}

	@Operation(summary = "내 운동기록 상세 조회", description = "로그인한 회원의 운동기록을 운동/세트 상세까지 포함하여 조회한다")
	@GetMapping("/workout-records/{workoutRecordId}")
	public ApiResponse<WorkoutRecordResponseDto> getWorkoutRecord(@PathVariable Long workoutRecordId) {

		return ApiResponse.ok(workoutRecordService.getWorkoutRecord(SecurityUtil.getUserId(), workoutRecordId));
	}

	@Operation(summary = "운동기록 등록", description = "로그인한 회원의 운동기록을 등록한다")
	@PostMapping("/workout-records")
	public ApiResponse<Void> insertWorkoutRecord(@Valid @RequestBody WorkoutRecordInsertDto dto) {

		workoutRecordService.insertWorkoutRecord(SecurityUtil.getUserId(), dto);
		return ApiResponse.ok();
	}

	@Operation(summary = "운동기록 수정", description = "로그인한 회원의 운동기록을 수정한다")
	@PutMapping("/workout-records/{workoutRecordId}")
	public ApiResponse<Void> updateWorkoutRecord(@PathVariable Long workoutRecordId, @Valid @RequestBody WorkoutRecordUpdateDto dto) {

		workoutRecordService.updateWorkoutRecord(SecurityUtil.getUserId(), workoutRecordId, dto);
		return ApiResponse.ok();
	}

	@Operation(summary = "운동기록 삭제", description = "로그인한 회원의 운동기록을 삭제한다")
	@DeleteMapping("/workout-records/{workoutRecordId}")
	public ApiResponse<Void> deleteWorkoutRecord(@PathVariable Long workoutRecordId) {

		workoutRecordService.deleteWorkoutRecord(SecurityUtil.getUserId(), workoutRecordId);
		return ApiResponse.ok();
	}

}
