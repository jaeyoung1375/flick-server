package kr.co.motive.workout.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutRecordExerciseResponseDto {

	@Schema(description = "상세기록아이디")
	private Long workoutRecordExerciseId;

	@Schema(description = "운동아이디")
	private Long exerciseId;

	@Schema(description = "운동명")
	private String exerciseName;

	@Schema(description = "상세기록 내 운동 정렬순서")
	private Integer sortNo;

	@Schema(description = "세트 목록")
	private List<WorkoutRecordSetResponseDto> sets;

}
