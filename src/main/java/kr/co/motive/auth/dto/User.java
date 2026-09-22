package kr.co.motive.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.motive.auth.enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class User {

    @Schema(description = "회원아이디")
    private Long userId;

    @Schema(description = "이메일")
    private String email;

    @Schema(description = "비밀번호 해시")
    private String passwordHash;

    @Schema(description = "계정상태 (ACTIVE, DEACTIVATE)")
    private String status;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "휴대폰번호")
    private String phone;

    @Schema(description = "권한")
    private Role role;

    @Schema(description = "등록일시")
    private LocalDateTime regDt;

    @Schema(description = "수정일시")
    private LocalDateTime modDt;

    @Schema(description = "프로필 파일아이디")
    private Long profileFileId;

    @Schema(description = "마지막 로그인일시")
    private LocalDateTime lastLoginDt;

    @Schema(description = "성별")
    private String gender;

    @Schema(description = "생년월일")
    private LocalDateTime birthDt;

    @Schema(description = "닉네임")
    private String nickname;
}
