package kr.co.motive.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthExchangeRequestDto {

    @Schema(description = "모바일 로그인 1회용 교환 코드")
    private String code;
}
