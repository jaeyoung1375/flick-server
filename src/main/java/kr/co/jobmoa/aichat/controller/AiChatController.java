package kr.co.jobmoa.aichat.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.jobmoa.aichat.dto.AiChatAskRequestDto;
import kr.co.jobmoa.aichat.dto.AiChatAskResponseDto;
import kr.co.jobmoa.aichat.service.AiChatService;
import kr.co.jobmoa.common.response.ApiResponse;
import kr.co.jobmoa.common.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "AI 챗봇 Controller", description = "운동 기록 기반 AI 챗봇 컨트롤러")
public class AiChatController {

	private final AiChatService aiChatService;

	@Operation(summary = "AI 챗봇 질문", description = "로그인한 회원의 운동 기록을 근거로 질문에 답변한다")
	@PostMapping("/ai-chat/ask")
	public ApiResponse<AiChatAskResponseDto> ask(@Valid @RequestBody AiChatAskRequestDto dto) {

		String answer = aiChatService.ask(SecurityUtil.getUserId(), dto.getQuestion());
		return ApiResponse.ok(AiChatAskResponseDto.builder().answer(answer).build());
	}

}
