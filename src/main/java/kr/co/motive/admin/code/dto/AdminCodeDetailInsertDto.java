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
public class AdminCodeDetailInsertDto {

	@Schema(description = "상세코드아이디")
	@NotBlank(message = "상세코드아이디는 필수입니다.")
	private String dtlCdId;

	@Schema(description = "상세코드명")
	@NotBlank(message = "상세코드명은 필수입니다.")
	private String dtlCdNm;

	@Schema(description = "상세코드설명")
	private String dtlCdExpln;

	@Schema(description = "연결상세코드아이디1")
	private String lnkgDtlCdId1;

	@Schema(description = "연결상세코드명1")
	private String lnkgDtlCdNm1;

	@Schema(description = "연결상세코드아이디2")
	private String lnkgDtlCdId2;

	@Schema(description = "연결상세코드명2")
	private String lnkgDtlCdNm2;

	@Schema(description = "사용여부")
	private String useYn;

	@Schema(description = "정렬순서")
	private Long sortSeq;

}