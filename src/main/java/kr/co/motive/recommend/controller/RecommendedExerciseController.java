package kr.co.motive.recommend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.motive.common.response.ApiResponse;
import kr.co.motive.recommend.dto.RecommendedExerciseResponseDto;
import kr.co.motive.recommend.service.RecommendedExerciseService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "추천 운동 Controller", description = "메인 홈 화면 추천 운동 카드 컨트롤러")
public class RecommendedExerciseController {

	private final RecommendedExerciseService recommendedExerciseService;

	@Operation(summary = "추천 운동 무작위 조회", description = "운영자가 등록한 추천 운동 풀 중 무작위 count개를 노출 순서로 정렬해 조회한다")
	@GetMapping("/recommended-exercises")
	public ApiResponse<List<RecommendedExerciseResponseDto>> getRandomRecommendedExercises(
			@RequestParam(defaultValue = "5") int count) {

		return ApiResponse.ok(recommendedExerciseService.getRandomRecommendedExercises(count));
	}

}
