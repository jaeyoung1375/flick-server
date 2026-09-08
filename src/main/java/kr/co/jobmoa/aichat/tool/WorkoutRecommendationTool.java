package kr.co.jobmoa.aichat.tool;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import kr.co.jobmoa.aichat.dto.NextWorkoutPartResult;
import kr.co.jobmoa.aichat.mapper.AiChatMapper;
import lombok.RequiredArgsConstructor;

/**
 * 개인화 운동 추천 도구. 키워드 매칭이 아니라 LLM Function Calling으로 호출된다 —
 * {@link kr.co.jobmoa.aichat.service.AiChatService}가 이 객체를 {@code ChatClient.tools(...)}로
 * 등록해두면, "오늘 어디 운동하지?" 같은 질문의 의미를 LLM이 스스로 판단해 호출한다.
 *
 * <p>분할 순서·부위코드 조합은 {@code RecommendedExercise-mapper.xml}에 이미 정리된
 * "헬스장에서 같이 하는 부위 조합"(등+이두 / 가슴+삼두 / 어깨+코어 / 하체, 부위코드 01~07)을
 * 그대로 재사용해 하체 → 등/이두 → 어깨/코어 → 가슴/삼두 순으로 도는 4일 분할로 구성했다.
 * 사용자별 커스텀 분할은 이번 범위가 아니다([06_domain_playbooks/ai-chat.md] 참고).
 */
@Component
@RequiredArgsConstructor
public class WorkoutRecommendationTool {

	private final AiChatMapper aiChatMapper;

	private record SplitDay(String label, Set<String> bodyPartCodes) {
	}

	private static final List<SplitDay> SPLIT_ORDER = List.of(
			new SplitDay("하체", Set.of("03")),
			new SplitDay("등/이두", Set.of("02", "06")),
			new SplitDay("어깨/코어", Set.of("04", "07")),
			new SplitDay("가슴/삼두", Set.of("01", "05")));

	@Tool(name = "getNextWorkoutPart",
			description = "로그인한 사용자의 최근 운동기록을 조회해 다음에 추천할 운동 부위(분할)를 계산한다. "
					+ "'오늘 어디 운동하지', '다음엔 뭐 하지'처럼 사용자 개인 운동 기록에 기반한 추천을 물을 때 호출한다.")
	public NextWorkoutPartResult getNextWorkoutPart(ToolContext toolContext) {

		Long userId = (Long) toolContext.getContext().get("userId");

		Long latestWorkoutRecordId = aiChatMapper.selectLatestWorkoutRecordId(userId);
		if (latestWorkoutRecordId == null) {
			// 신규 유저 등 운동기록이 아예 없는 경우 — 에러가 아니라 첫 분할(하체)부터 추천
			return NextWorkoutPartResult.builder()
					.hasWorkoutHistory(false)
					.recentSplitDay(null)
					.recommendedSplitDay(SPLIT_ORDER.get(0).label())
					.build();
		}

		List<String> recentBodyPartCodes = aiChatMapper.selectBodyPartCodesByWorkoutRecordId(latestWorkoutRecordId);
		int matchedIndex = findMatchedSplitDayIndex(recentBodyPartCodes);

		if (matchedIndex == -1) {
			// 기록은 있지만 정의된 분할 부위코드와 매칭되지 않는 경우 — 첫 분할(하체)부터 추천
			return NextWorkoutPartResult.builder()
					.hasWorkoutHistory(true)
					.recentSplitDay(null)
					.recommendedSplitDay(SPLIT_ORDER.get(0).label())
					.build();
		}

		int nextIndex = (matchedIndex + 1) % SPLIT_ORDER.size();

		return NextWorkoutPartResult.builder()
				.hasWorkoutHistory(true)
				.recentSplitDay(SPLIT_ORDER.get(matchedIndex).label())
				.recommendedSplitDay(SPLIT_ORDER.get(nextIndex).label())
				.build();
	}

	private int findMatchedSplitDayIndex(List<String> bodyPartCodes) {

		for (int i = 0; i < SPLIT_ORDER.size(); i++) {
			if (!Collections.disjoint(SPLIT_ORDER.get(i).bodyPartCodes(), bodyPartCodes)) {
				return i;
			}
		}

		return -1;
	}

}
