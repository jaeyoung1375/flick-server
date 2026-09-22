package kr.co.motive.workout.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
public class WorkoutRecordUpdateDto {

	@Schema(description = "카테고리코드(헬스/홈트/러닝/등산/야외)")
	@NotBlank(message = "카테고리코드는 필수입니다.")
	private String categoryCd;

	@Schema(description = "기록일시")
	@NotNull(message = "기록일시는 필수입니다.")
	private LocalDateTime recordDt;

	@Schema(description = "운동시간(분)")
	private Integer durationMin;

	@Schema(description = "운동 목록")
	@NotEmpty(message = "운동은 1개 이상이어야 합니다.")
	@Valid
	private List<WorkoutRecordExerciseRequestDto> exercises;

}
