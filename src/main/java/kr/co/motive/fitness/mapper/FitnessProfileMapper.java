package kr.co.motive.fitness.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.motive.fitness.dto.UserFitnessProfileInsertDto;
import kr.co.motive.fitness.dto.UserFitnessProfileResponseDto;
import kr.co.motive.fitness.dto.UserFitnessProfileUpdateDto;

@Mapper
public interface FitnessProfileMapper {

	/**
	 * 운동프로필 조회
	 * @param userId
	 * @return
	 */
	UserFitnessProfileResponseDto getProfile(@Param("userId") Long userId);

	/**
	 * 운동프로필 존재 여부 조회
	 * @param userId
	 * @return
	 */
	boolean existsProfile(@Param("userId") Long userId);

	/**
	 * 운동프로필 등록
	 * @param userId
	 * @param dto
	 */
	void insertProfile(@Param("userId") Long userId, @Param("dto") UserFitnessProfileInsertDto dto);

	/**
	 * 운동프로필 수정
	 * @param userId
	 * @param dto
	 * @return 수정된 행 수
	 */
	int updateProfile(@Param("userId") Long userId, @Param("dto") UserFitnessProfileUpdateDto dto);

}