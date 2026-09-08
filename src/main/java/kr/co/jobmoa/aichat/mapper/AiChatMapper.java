package kr.co.jobmoa.aichat.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AiChatMapper {

	/**
	 * 특정 회원의 가장 최근 운동기록 ID 조회 (기록이 없으면 null)
	 * @param userId
	 * @return
	 */
	Long selectLatestWorkoutRecordId(@Param("userId") Long userId);

	/**
	 * 특정 운동기록에 포함된 운동들의 대표부위코드(EXERCISES.BODY_PART_CD) 목록 조회 (중복 제거)
	 * @param workoutRecordId
	 * @return
	 */
	List<String> selectBodyPartCodesByWorkoutRecordId(@Param("workoutRecordId") Long workoutRecordId);

}
