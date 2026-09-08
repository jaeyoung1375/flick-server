package kr.co.jobmoa.aichat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatAskRequestDto {

	@Schema(description = "질문", example = "오늘 어디 운동하지?")
	@NotBlank(message = "질문은 필수입니다.")
	private String question;

}
