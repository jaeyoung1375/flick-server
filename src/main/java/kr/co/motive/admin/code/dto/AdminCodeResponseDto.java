package kr.co.motive.admin.code.dto;

import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCodeResponseDto {

	@Schema(description = "공통코드아이디")
	private String comCdId;

	@Schema(description = "공통코드명")
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

	@Schema(description = "등록아이디")
	private String regId;

	@Schema(description = "등록일시")
	private Date regDt;

	@Schema(description = "변경아이디")
	private String modId;

	@Schema(description = "변경일시")
	private Date modDt;

}