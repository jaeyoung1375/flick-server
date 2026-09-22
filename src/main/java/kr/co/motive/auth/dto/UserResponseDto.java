package kr.co.motive.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {

    @Schema(description = "액세스토큰")
    private String accessToken;

    @Schema(description = "리프레시토큰")
    private String refreshToken;

    @Schema(description = "신규가입 여부")
    private boolean isNew;
}
