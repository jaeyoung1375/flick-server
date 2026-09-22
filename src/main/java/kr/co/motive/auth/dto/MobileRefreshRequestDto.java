package kr.co.motive.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MobileRefreshRequestDto {

    @Schema(description = "모바일 클라이언트가 보관 중인 refresh token")
    private String refreshToken;
}
