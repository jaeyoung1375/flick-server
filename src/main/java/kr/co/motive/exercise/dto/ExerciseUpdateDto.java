package kr.co.motive.exercise.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
public class ExerciseUpdateDto {

	@Schema(description = "운동명")
	@NotBlank(message = "운동명은 필수입니다.")
	private String name;

	@Schema(description = "대표부위코드")
	@NotBlank(message = "대표부위코드는 필수입니다.")
	private String bodyPartCd;

	@Schema(description = "요구장비코드")
	private String equipmentCd;

	@Schema(description = "이미지파일아이디")
	private Long imageFileId;

}
