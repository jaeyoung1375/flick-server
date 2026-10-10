package kr.co.flick.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JoinRequestDto {

    @Schema(description = "이메일")
    private String email;
    @Schema(description = "비밀번호")
    private String password;
    @Schema(description = "닉네임")
    private String nickname;
}
