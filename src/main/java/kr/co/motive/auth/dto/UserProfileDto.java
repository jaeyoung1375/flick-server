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
public class UserProfileDto {

    @Schema(description = "회원아이디")
    private Long userId;

    @Schema(description = "이메일")
    private String email;

    @Schema(description = "계정상태 (ACTIVE, DEACTIVATE)")
    private String status;

    @Schema(description = "권한")
    private Role role;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "프로필 파일아이디")
    private Long profileFileId;

    @Schema(description = "성별")
    private String gender;

    @Schema(description = "생년월일")
    private LocalDateTime birthDt;

    public static UserProfileDto from(User user) {
        return UserProfileDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .status(user.getStatus())
                .role(user.getRole())
                .name(user.getName())
                .profileFileId(user.getProfileFileId())
                .gender(user.getGender())
                .birthDt(user.getBirthDt())
                .build();
    }


}
