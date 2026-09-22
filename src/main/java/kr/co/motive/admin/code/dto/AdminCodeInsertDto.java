package kr.co.motive.admin.code.dto;

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
public class AdminCodeInsertDto {

	@Schema(description = "공통코드아이디")
	@NotBlank(message = "공통코드아이디는 필수입니다.")
	private String comCdId;

	@Schema(description = "공통코드명")
	@NotBlank(message = "공통코드명은 필수입니다.")
	private String comCdNm;

	@Schema(description = "공통코드설명")
	private String comCdExpln;

	@Schema(description = "연결공통코드아이디1")
	private String linkComCdId1;

	@Schema(description = "연결공통코드아이디2")
	private String linkComCdId2;

	@Schema(description = "사용여부")
	private String useYn;

	@Schema(description = "정렬순서")
	private Long sortSeq;

}