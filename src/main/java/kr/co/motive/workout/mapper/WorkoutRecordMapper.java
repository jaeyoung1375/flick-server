package kr.co.motive.workout.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.motive.workout.dto.WorkoutRecordExerciseRequestDto;
import kr.co.motive.workout.dto.WorkoutRecordInsertDto;
import kr.co.motive.workout.dto.WorkoutRecordResponseDto;
import kr.co.motive.workout.dto.WorkoutRecordSetRequestDto;
import kr.co.motive.workout.dto.WorkoutRecordUpdateDto;

@Mapper
public interface WorkoutRecordMapper {

	/**
	 * 운동기록 목록 조회 (운동/세트 상세는 포함하지 않음)
	 * @param userId
	 * @return
	 */
	List<WorkoutRecordResponseDto> getWorkoutRecordList(@Param("userId") Long userId);

	/**
	 * 운동기록 상세 조회 (운동/세트 상세 포함, 한 번의 쿼리로 조회)
	 * @param userId
	 * @param workoutRecordId
	 * @return
	 */
	WorkoutRecordResponseDto getWorkoutRecord(@Param("userId") Long userId, @Param("workoutRecordId") Long workoutRecordId);

	/** WORKOUT_RECORD_SEQ 채번 */
	Long nextWorkoutRecordId();

	/** WORKOUT_RECORD_EXERCISE_SEQ 채번 */
	Long nextWorkoutRecordExerciseId();

	/** WORKOUT_RECORD_SET_SEQ 채번 */
	Long nextWorkoutRecordSetId();

	/**
	 * 운동기록 헤더 등록
	 * @param workoutRecordId
	 * @param userId
	 * @param dto
	 */
	void insertWorkoutRecord(@Param("workoutRecordId") Long workoutRecordId, @Param("userId") Long userId,
			@Param("dto") WorkoutRecordInsertDto dto);

	/**
	 * 상세기록(운동) 등록
	 * @param workoutRecordExerciseId
	 * @param workoutRecordId
	 * @param dto
	 */
	void insertWorkoutRecordExercise(@Param("workoutRecordExerciseId") Long workoutRecordExerciseId,
			@Param("workoutRecordId") Long workoutRecordId, @Param("dto") WorkoutRecordExerciseRequestDto dto);

	/**
	 * 세트 등록
	 * @param workoutRecordSetId
	 * @param workoutRecordExerciseId
	 * @param dto
	 */
	void insertWorkoutRecordSet(@Param("workoutRecordSetId") Long workoutRecordSetId,
			@Param("workoutRecordExerciseId") Long workoutRecordExerciseId, @Param("dto") WorkoutRecordSetRequestDto dto);

	/**
	 * 운동기록 헤더 수정
	 * @param userId
	 * @param workoutRecordId
	 * @param dto
	 * @return 수정된 행 수
	 */
	int updateWorkoutRecord(@Param("userId") Long userId, @Param("workoutRecordId") Long workoutRecordId,
			@Param("dto") WorkoutRecordUpdateDto dto);

	/**
	 * 운동기록에 속한 상세기록(운동) 전체 삭제 (세트는 FK ON DELETE CASCADE로 함께 삭제)
	 * @param workoutRecordId
	 */
	void deleteWorkoutRecordExerciseByRecordId(@Param("workoutRecordId") Long workoutRecordId);

	/**
	 * 운동기록 삭제 (상세기록/세트는 FK ON DELETE CASCADE로 함께 삭제)
	 * @param userId
	 * @param workoutRecordId
	 * @return 삭제된 행 수
	 */
	int deleteWorkoutRecord(@Param("userId") Long userId, @Param("workoutRecordId") Long workoutRecordId);

}
