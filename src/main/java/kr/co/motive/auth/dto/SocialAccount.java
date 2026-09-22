package kr.co.motive.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.motive.auth.enums.Provider;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SocialAccount {

    @Schema(description = "소셜계정아이디")
    private Long socialId;

    @Schema(description = "회원아이디")
    private Long userId;

    @Schema(description = "소셜 로그인 제공자")
    private Provider provider;

    @Schema(description = "제공자측 사용자 고유아이디")
    private String providerUserId;

    @Schema(description = "등록일시")
    private LocalDateTime regDt;

    @Schema(description = "변경일시")
    private LocalDateTime modDt;

    @Schema(description = "프로필파일아이디")
    private Long profileFileId;

    @Schema(description = "제공자 액세스토큰(암호화)")
    private String providerAccessToken;

    @Schema(description = "제공자 액세스토큰 만료일시")
    private LocalDateTime expiresAt;
}
