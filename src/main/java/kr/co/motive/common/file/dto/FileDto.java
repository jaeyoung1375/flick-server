package kr.co.motive.common.file.dto;

import java.util.Date;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class FileDto {

	@Schema(description = "파일아이디")
	private long fileId;

	@Schema(description = "원본파일명")
	private String orgFileNm;

	@Schema(description = "저장파일명")
	private String saveFileNm;

	@Schema(description = "파일경로")
	private String filePath;

	@Schema(description = "파일크기")
	private long fileSize;

	@Schema(description = "파일확장자")
	private String fileExt;

	@Schema(description = "임시파일여부")
	private String tempYn;

	@Schema(description = "임시파일키")
	private String tempKey;

	@Schema(description = "삭제여부")
	private String delYn;

	@Schema(description = "삭제일자")
	private Date delDt;


}
