package kr.co.motive.recommend.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.co.motive.recommend.dto.RecommendedExerciseResponseDto;

@Mapper
public interface RecommendedExerciseMapper {

	/**
	 * "오늘의 조합"(1~4)을 무작위로 하나 고른다
	 * @return
	 */
	int pickRandomGroup();

	/**
	 * [groupNo] 조합에 속한 부위의 추천 운동 중 무작위 [count]개를 노출 순서(SORT_NO)로 정렬해 조회
	 * @param groupNo
	 * @param count
	 * @return
	 */
	List<RecommendedExerciseResponseDto> getRandomRecommendedExercisesByGroup(@Param("groupNo") int groupNo,
			@Param("count") int count);

}
