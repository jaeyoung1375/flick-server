package kr.co.motive.workout.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.motive.common.code.WorkoutErrorCode;
import kr.co.motive.common.exception.CustomException;
import kr.co.motive.workout.dto.WorkoutRecordExerciseRequestDto;
import kr.co.motive.workout.dto.WorkoutRecordInsertDto;
import kr.co.motive.workout.dto.WorkoutRecordResponseDto;
import kr.co.motive.workout.dto.WorkoutRecordUpdateDto;
import kr.co.motive.workout.mapper.WorkoutRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutRecordService {

	private final WorkoutRecordMapper workoutRecordMapper;

	/**
	 * 운동기록 목록 조회 (운동/세트 상세는 포함하지 않음)
	 * @param userId
	 * @return
	 */
	public List<WorkoutRecordResponseDto> getWorkoutRecordList(Long userId) {

		return workoutRecordMapper.getWorkoutRecordList(userId);
	}

	/**
	 * 운동기록 상세 조회 (운동/세트 상세 포함, 한 번의 쿼리로 조회)
	 * @param userId
	 * @param workoutRecordId
	 * @return
	 */
	public WorkoutRecordResponseDto getWorkoutRecord(Long userId, Long workoutRecordId) {

		WorkoutRecordResponseDto record = workoutRecordMapper.getWorkoutRecord(userId, workoutRecordId);
		if (record == null) {
			throw new CustomException(WorkoutErrorCode.RECORD_NOT_FOUND);
		}

		return record;
	}

	/**
	 * 운동기록 등록
	 * @param userId
	 * @param dto
	 */
	@Transactional
	public void insertWorkoutRecord(Long userId, WorkoutRecordInsertDto dto) {

		Long workoutRecordId = workoutRecordMapper.nextWorkoutRecordId();
		workoutRecordMapper.insertWorkoutRecord(workoutRecordId, userId, dto);

		insertExercises(workoutRecordId, dto.getExercises());
	}

	/**
	 * 운동기록 수정 (운동/세트는 전체 삭제 후 재등록)
	 * @param userId
	 * @param workoutRecordId
	 * @param dto
	 */
	@Transactional
	public void updateWorkoutRecord(Long userId, Long workoutRecordId, WorkoutRecordUpdateDto dto) {

		int updated = workoutRecordMapper.updateWorkoutRecord(userId, workoutRecordId, dto);
		if (updated == 0) {
			throw new CustomException(WorkoutErrorCode.RECORD_NOT_FOUND);
		}

		workoutRecordMapper.deleteWorkoutRecordExerciseByRecordId(workoutRecordId);
		insertExercises(workoutRecordId, dto.getExercises());
	}

	/**
	 * 운동기록 삭제 (상세기록/세트는 FK ON DELETE CASCADE로 함께 삭제)
	 * @param userId
	 * @param workoutRecordId
	 */
	public void deleteWorkoutRecord(Long userId, Long workoutRecordId) {

		int deleted = workoutRecordMapper.deleteWorkoutRecord(userId, workoutRecordId);
		if (deleted == 0) {
			throw new CustomException(WorkoutErrorCode.RECORD_NOT_FOUND);
		}
	}

	private void insertExercises(Long workoutRecordId, List<WorkoutRecordExerciseRequestDto> exercises) {

		for (WorkoutRecordExerciseRequestDto exercise : exercises) {
			Long workoutRecordExerciseId = workoutRecordMapper.nextWorkoutRecordExerciseId();
			workoutRecordMapper.insertWorkoutRecordExercise(workoutRecordExerciseId, workoutRecordId, exercise);

			exercise.getSets().forEach(set -> {
				Long workoutRecordSetId = workoutRecordMapper.nextWorkoutRecordSetId();
				workoutRecordMapper.insertWorkoutRecordSet(workoutRecordSetId, workoutRecordExerciseId, set);
			});
		}
	}

}
