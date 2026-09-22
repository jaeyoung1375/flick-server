package kr.co.motive.recommend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import kr.co.motive.recommend.dto.RecommendedExerciseResponseDto;
import kr.co.motive.recommend.mapper.RecommendedExerciseMapper;
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
