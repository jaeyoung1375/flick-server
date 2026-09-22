package kr.co.motive.admin.exercise.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.motive.exercise.dto.ExerciseInsertDto;
import kr.co.motive.exercise.dto.ExerciseResponseDto;
import kr.co.motive.exercise.dto.ExerciseUpdateDto;

@Mapper
public interface AdminExerciseMapper {

	/**
	 * 운동 목록 조회
	 * @param bodyPartCd 부위코드
	 * @param equipmentCd 기구코드
	 * @param name 운동명
	 * @return
	 */
	List<ExerciseResponseDto> getExerciseList(@Param("bodyPartCd") String bodyPartCd,
			@Param("equipmentCd") String equipmentCd, @Param("name") String name);

	/**
	 * 운동 상세 조회
	 * @param exerciseId
	 * @return
	 */
	ExerciseResponseDto getExercise(@Param("exerciseId") Long exerciseId);

	/**
	 * 운동 등록
	 * @param dto
	 */
	void insertExercise(@Param("dto") ExerciseInsertDto dto);

	/**
	 * 운동 수정
	 * @param exerciseId
	 * @param dto
	 * @return 수정된 행 수
	 */
	int updateExercise(@Param("exerciseId") Long exerciseId, @Param("dto") ExerciseUpdateDto dto);

	/**
	 * 운동 삭제
	 * @param exerciseId
	 * @return 삭제된 행 수
	 */
	int deleteExercise(@Param("exerciseId") Long exerciseId);

}
