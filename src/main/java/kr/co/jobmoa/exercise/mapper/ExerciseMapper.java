package kr.co.jobmoa.exercise.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.jobmoa.exercise.dto.ExerciseResponseDto;

@Mapper
public interface ExerciseMapper {

	/**
	 * 운동 조회
	 * @param exerciseId
	 * @return
	 */
	ExerciseResponseDto getExercise(@Param("exerciseId") Long exerciseId);

	/**
	 * 운동 목록 조회
	 * @return
	 */
	List<ExerciseResponseDto> getExerciseList();

}
