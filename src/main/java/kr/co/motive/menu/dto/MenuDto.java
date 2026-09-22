package kr.co.motive.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "메뉴 DTO")
public class MenuDto {

	@Schema(description = "메뉴아이디")
	private String menuId;

	@Schema(description = "메뉴명")
	private String menuNm;

	@Schema(description = "메뉴URL")
	private String menuUrl;

	@Schema(description = "메뉴레벨")
	private String menuLevel;

	@Schema(description = "메뉴정렬순서")
	private String menuOrd;

	@Schema(description = "상위메뉴아이디")
	private String upMenuId;

	@Schema(description = "상위여부")
	private String topYn;

	@Schema(description = "사용여부")
	private String useYn;

	@Schema(description = "동록자")
	private String regId;

	@Schema(description = "동록자")
	private String regDt;

}
