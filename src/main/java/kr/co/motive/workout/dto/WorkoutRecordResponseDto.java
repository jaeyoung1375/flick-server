package kr.co.motive.workout.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutRecordResponseDto {

	@Schema(description = "운동기록아이디")
	private Long workoutRecordId;

	@Schema(description = "회원아이디")
	private Long userId;

	@Schema(description = "카테고리코드(헬스/홈트/러닝/등산/야외)")
	private String categoryCd;

	@Schema(description = "기록일시")
	@JsonFormat(pattern = "yyyyMMddHHmm")
	private LocalDateTime recordDt;

	@Schema(description = "운동시간(분)")
	private Integer durationMin;

	@Schema(description = "등록일시")
	private LocalDateTime regDt;

	@Schema(description = "변경일시")
	private LocalDateTime modDt;

	@Schema(description = "운동 목록")
	private List<WorkoutRecordExerciseResponseDto> exercises;

	@Schema(description = "부위명 목록(쉼표 구분, 중복 제거) — 목록 조회에서만 채워짐")
	private String bodyPartNms;

}
