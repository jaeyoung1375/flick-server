package kr.co.motive.exercise.service;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.motive.common.code.ExerciseErrorCode;
import kr.co.motive.common.exception.CustomException;
import kr.co.motive.exercise.dto.ExerciseResponseDto;
import kr.co.motive.exercise.mapper.ExerciseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExerciseService {

	private final ExerciseMapper exerciseMapper;

	/**
	 * 운동 목록 조회
	 * @return
	 */
	public List<ExerciseResponseDto> getExerciseList() {

		return exerciseMapper.getExerciseList();
	}

	/**
	 * 운동 상세 조회
	 * @param exerciseId
	 * @return
	 */
	public ExerciseResponseDto getExercise(Long exerciseId) {

		ExerciseResponseDto exercise = exerciseMapper.getExercise(exerciseId);
		if (exercise == null) {
			throw new CustomException(ExerciseErrorCode.EXERCISE_NOT_FOUND);
		}

		return exercise;
	}

}
