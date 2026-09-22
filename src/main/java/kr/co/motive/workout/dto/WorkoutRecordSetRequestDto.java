package kr.co.motive.workout.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
public class WorkoutRecordSetRequestDto {

	@Schema(description = "세트번호")
	@NotNull(message = "세트번호는 필수입니다.")
	private Integer setNo;

	@Schema(description = "중량(kg)")
	private BigDecimal weight;

	@Schema(description = "횟수")
	private Integer reps;

}
