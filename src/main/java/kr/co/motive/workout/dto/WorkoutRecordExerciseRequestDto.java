package kr.co.motive.workout.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
public class WorkoutRecordExerciseRequestDto {

	@Schema(description = "운동아이디")
	@NotNull(message = "운동아이디는 필수입니다.")
	private Long exerciseId;

	@Schema(description = "상세기록 내 운동 정렬순서")
	private Integer sortNo;

	@Schema(description = "세트 목록")
	@NotEmpty(message = "세트는 1개 이상이어야 합니다.")
	@Valid
	private List<WorkoutRecordSetRequestDto> sets;

}
