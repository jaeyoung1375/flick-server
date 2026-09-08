package kr.co.jobmoa.aichat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * {@code getNextWorkoutPart} 도구의 반환값. 컨트롤러 응답이 아니라 LLM에게 그대로
 * JSON으로 전달되어 최종 답변 문장을 만드는 데 쓰인다 — API 계약(Schema) 대상이 아니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NextWorkoutPartResult {

	/** 운동기록이 1건이라도 있는지 여부 (신규 유저 판별용) */
	private boolean hasWorkoutHistory;

	/** 가장 최근 운동한 분할 (매칭되는 분할이 없으면 null) */
	private String recentSplitDay;

	/** 다음에 추천하는 분할 */
	private String recommendedSplitDay;

}
