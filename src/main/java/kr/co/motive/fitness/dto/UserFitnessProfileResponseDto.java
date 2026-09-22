package kr.co.motive.fitness.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFitnessProfileResponseDto {

	@Schema(description = "회원아이디")
	private Long userId;

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

	@Schema(description = "스쿼트 가능여부")
	private String squatYn;

	@Schema(description = "벤치프레스 가능여부")
	private String benchPressYn;

	@Schema(description = "등록일시")
	private LocalDateTime regDt;

	@Schema(description = "변경일시")
	private LocalDateTime modDt;

}