package kr.co.jobmoa.aichat.service;

import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import kr.co.jobmoa.aichat.tool.WorkoutRecommendationTool;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiChatService {

	private static final String SYSTEM_PROMPT = """
			당신은 운동 기록 앱 jobmoa의 운동 코치 챗봇입니다.
			사용자가 자신의 최근 운동 기록을 바탕으로 다음에 할 운동 부위를 물으면
			반드시 제공된 도구(getNextWorkoutPart)를 호출해 그 결과로 답변하세요. 도구를 호출하지 않고 추측하지 마세요.
			운동 자세·이론 등 개인 기록과 무관한 일반적인 헬스 지식 질문은 도구를 호출하지 말고
			당신이 알고 있는 일반 지식으로 답변하세요.
			""";

	private final ChatClient chatClient;
	private final WorkoutRecommendationTool workoutRecommendationTool;

	/**
	 * 사용자 질문에 답변한다. 개인화 질문("오늘 어디 운동하지?" 등)이면 LLM이 Function Calling으로
	 * {@link WorkoutRecommendationTool#getNextWorkoutPart}를 호출해 실제 운동기록 기반으로 답한다.
	 * 일반 지식 질문은 도구 호출 없이 LLM 자체 지식으로 답한다(RAG는 이번 범위 아님).
	 * @param userId 로그인한 회원 ID ({@code SecurityUtil.getUserId()}로 얻은 값만 전달할 것)
	 * @param question 사용자 질문
	 * @return 답변
	 */
	public String ask(Long userId, String question) {

		return chatClient.prompt()
				.system(SYSTEM_PROMPT)
				.user(question)
				.tools(workoutRecommendationTool)
				.toolContext(Map.of("userId", userId))
				.call()
				.content();
	}

}
