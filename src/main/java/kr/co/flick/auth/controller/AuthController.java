package kr.co.flick.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.flick.auth.dto.TokenResponseDto;
import kr.co.flick.auth.dto.User;
import kr.co.flick.auth.dto.UserProfileDto;
import kr.co.flick.auth.dto.UserResponseDto;
import kr.co.flick.auth.service.AuthService;
import kr.co.flick.common.code.UserErrorCode;
import kr.co.flick.common.exception.CustomException;
import kr.co.flick.common.response.ApiResponse;
import kr.co.flick.common.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/refresh")
    public ApiResponse<TokenResponseDto> refresh(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        
        if(refreshToken == null){
            throw new CustomException(UserErrorCode.INVALID_REFRESH_TOKEN);
        }

        UserResponseDto result = authService.refresh(refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", result.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofDays(14))
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        TokenResponseDto responseDto = TokenResponseDto
                .builder()
                .accessToken(result.getAccessToken())
                .isNew(result.isNew())
                .build();

        return ApiResponse.ok(responseDto);

    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(){
        Long userId = SecurityUtil.getUserId();
        authService.logout(userId);

        return ApiResponse.ok();
    }

    @GetMapping("/me")
    public ApiResponse<UserProfileDto> getMe(HttpServletRequest request){

        Long userId = SecurityUtil.getUserId();

        User user = authService.findUser(userId);

        if(user == null){
            throw new CustomException(UserErrorCode.USER_NOT_FOUND);
        }

        return ApiResponse.ok(UserProfileDto.from(user));
    }
}
