package kr.co.motive.admin.exercise.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;

import kr.co.motive.admin.exercise.mapper.AdminExerciseMapper;
import kr.co.motive.common.code.ExerciseErrorCode;
import kr.co.motive.common.exception.CustomException;
import kr.co.motive.common.util.PageResponseDto;
import kr.co.motive.exercise.dto.ExerciseInsertDto;
import kr.co.motive.exercise.dto.ExerciseResponseDto;
import kr.co.motive.exercise.dto.ExerciseUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminExerciseService {

	private final AdminExerciseMapper adminExerciseMapper;

	/**
	 * 운동 목록 조회
	 * @param bodyPartCd 부위코드
	 * @param equipmentCd 기구코드
	 * @param name 운동명
	 * @param pageNum 페이지 번호 (1-based)
	 * @param pageSize 페이지 크기
	 * @return
	 */
	public PageResponseDto<ExerciseResponseDto> getExerciseList(String bodyPartCd, String equipmentCd, String name,
			int pageNum, int pageSize) {

		PageHelper.startPage(pageNum, pageSize);
		List<ExerciseResponseDto> list = adminExerciseMapper.getExerciseList(bodyPartCd, equipmentCd, name);
		return PageResponseDto.of(list);
	}

	/**
	 * 운동 상세 조회
	 * @param exerciseId
	 * @return
	 */
	public ExerciseResponseDto getExercise(Long exerciseId) {

		ExerciseResponseDto exercise = adminExerciseMapper.getExercise(exerciseId);
		if (exercise == null) {
			throw new CustomException(ExerciseErrorCode.EXERCISE_NOT_FOUND);
		}

		return exercise;
	}

	/**
	 * 운동 등록
	 * @param dto
	 */
	public void insertExercise(ExerciseInsertDto dto) {

		adminExerciseMapper.insertExercise(dto);
	}

	/**
	 * 운동 수정
	 * @param exerciseId
	 * @param dto
	 */
	public void updateExercise(Long exerciseId, ExerciseUpdateDto dto) {

		int updated = adminExerciseMapper.updateExercise(exerciseId, dto);
		if (updated == 0) {
			throw new CustomException(ExerciseErrorCode.EXERCISE_NOT_FOUND);
		}
	}

	/**
	 * 운동 삭제
	 * @param exerciseId
	 */
	public void deleteExercise(Long exerciseId) {

		int deleted = adminExerciseMapper.deleteExercise(exerciseId);
		if (deleted == 0) {
			throw new CustomException(ExerciseErrorCode.EXERCISE_NOT_FOUND);
		}
	}

}
