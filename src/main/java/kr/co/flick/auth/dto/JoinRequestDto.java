package kr.co.flick.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JoinRequestDto {

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식이 아닙니다.")
    @Schema(description = "이메일")
    private String email;
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\p{Punct})[A-Za-z0-9\\p{Punct}]{8,16}$",
            message = "비밀번호는 특수문자, 대소문자를 포함하여 8~16자 내로 입력해주세요."
    )
    @Schema(description = "비밀번호")
    private String password;
    @NotBlank(message = "닉네임을 입력해주세요.")
    @Schema(description = "닉네임")
    private String nickname;
}
