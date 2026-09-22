package kr.co.motive.workout.dto;

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
public class WorkoutRecordSetResponseDto {

	@Schema(description = "세트기록아이디")
	private Long workoutRecordSetId;

	@Schema(description = "세트번호")
	private Integer setNo;

	@Schema(description = "중량(kg)")
	private BigDecimal weight;

	@Schema(description = "횟수")
	private Integer reps;

}
