package kr.co.motive.exercise.dto;

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
public class ExerciseResponseDto {

	@Schema(description = "운동아이디")
	private Long exerciseId;

	@Schema(description = "운동명")
	private String name;

	@Schema(description = "대표부위코드")
	private String bodyPartCd;

	@Schema(description = "대표부위코드명")
	private String bodyPartNm;

	@Schema(description = "요구장비코드")
	private String equipmentCd;

	@Schema(description = "요구장비코드명")
	private String equipmentNm;

	@Schema(description = "이미지파일아이디")
	private Long imageFileId;

	@Schema(description = "등록일시")
	private LocalDateTime regDt;

	@Schema(description = "변경일시")
	private LocalDateTime modDt;

}
