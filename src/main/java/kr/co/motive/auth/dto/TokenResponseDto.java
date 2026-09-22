package kr.co.motive.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TokenResponseDto {

    private String accessToken;
    private boolean isNew;
    private boolean onboardingCompleted;
}
