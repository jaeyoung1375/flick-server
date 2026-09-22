package kr.co.motive.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.motive.auth.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserInsertDto {

    @Schema(description = "회원아이디 (insert 후 시퀀스값이 채워짐)")
    private Long userId;

    @Schema(description = "이메일")
    private String email;

    @Schema(description = "비밀번호 해시 (소셜 전용 가입자는 null)")
    private String passwordHash;

    @Schema(description = "계정상태 (ACTIVE, DEACTIVATE)")
    private String status;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "휴대폰번호")
    private String phone;

    @Schema(description = "권한 (USER, ADMIN)")
    private Role role;

    @Schema(description = "등록일시")
    private LocalDateTime regDt;

    @Schema(description = "변경일시")
    private LocalDateTime modDt;

    @Schema(description = "프로필파일아이디")
    private Long profileFileId;

    @Schema(description = "최근로그인일시")
    private LocalDateTime lastLoginDt;

    @Schema(description = "성별")
    private String gender;

    @Schema(description = "생년월일")
    private LocalDateTime birth;

}
