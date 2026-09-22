package kr.co.motive.recommend.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedExerciseResponseDto {

	@Schema(description = "추천운동아이디")
	private Long recommendedExerciseId;

	@Schema(description = "운동아이디")
	private Long exerciseId;

	@Schema(description = "운동명")
	private String exerciseName;

	@Schema(description = "부위코드명")
	private String bodyPartNm;

	@Schema(description = "이미지파일아이디")
	private Long imageFileId;

	@Schema(description = "권장 세트 수")
	private Integer sets;

	@Schema(description = "권장 중량(kg)")
	private BigDecimal weight;

	@Schema(description = "권장 횟수(회)")
	private Integer reps;

	@Schema(description = "예상 소요 시간(분)")
	private Integer durationMin;

	@Schema(description = "노출 순서")
	private Integer sortNo;

}
