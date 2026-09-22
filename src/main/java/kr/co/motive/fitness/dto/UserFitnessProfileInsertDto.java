package kr.co.motive.fitness.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class UserFitnessProfileInsertDto {

	@Schema(description = "운동경력코드")
	private String experienceCd;

	@Schema(description = "운동레벨코드")
	private String levelCd;

	@Schema(description = "보유장비코드")
	private String equipmentCd;

	@Schema(description = "키(cm)")
	private BigDecimal height;

	@Schema(description = "체중(kg)")
	private BigDecimal weight;

	@Schema(description = "목표체중(kg)")
	private BigDecimal goalWeight;

	@Schema(description = "헬스장아이디")
	private Long gymId;

	@Schema(description = "스쿼트 가능여부 (Y, N)")
	private String squatYn;

	@Schema(description = "벤치프레스 가능여부 (Y, N)")
	private String benchPressYn;

	@Schema(description = "닉네임")
	private String nickname;

	@Schema(description = "성별")
	private String gender;

	@Schema(description = "생년월일 (YYYY-MM-DD)")
	private String birth;

}