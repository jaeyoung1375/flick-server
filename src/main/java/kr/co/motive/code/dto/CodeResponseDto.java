package kr.co.motive.code.dto;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeResponseDto {

	@Schema(description = "공통코드아이디")
	private String comCdId;

	@Schema(description = "공통코드명")
	private String comCdNm;

	@Schema(description = "상세코드아이디")
	private String dtlCdId;

	@Schema(description = "상세코드명")
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

	@Schema(description = "등록아이디")
	private String regId;

	@Schema(description = "등록일시")
	private Date regDt;

	@Schema(description = "변경아이디")
	private String modId;

	@Schema(description = "변경일시")
	private Date modDt;



}
