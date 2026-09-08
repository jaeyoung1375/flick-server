package kr.co.jobmoa.recommend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.jobmoa.recommend.dto.RecommendedExerciseResponseDto;
import kr.co.jobmoa.recommend.mapper.RecommendedExerciseMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendedExerciseService {

	private final RecommendedExerciseMapper recommendedExerciseMapper;

	public List<RecommendedExerciseResponseDto> getRandomRecommendedExercises(int count) {

		int groupNo = recommendedExerciseMapper.pickRandomGroup();
		return recommendedExerciseMapper.getRandomRecommendedExercisesByGroup(groupNo, count);
	}

}
