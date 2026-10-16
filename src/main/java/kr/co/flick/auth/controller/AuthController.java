package kr.co.flick.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.co.flick.auth.dto.*;
import kr.co.flick.auth.service.AuthService;
import kr.co.flick.common.code.UserErrorCode;
import kr.co.flick.common.exception.CustomException;
import kr.co.flick.common.response.ApiResponse;
import kr.co.flick.common.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.Value;
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

        addRefreshTokenCookie(response, result);

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

    @PostMapping("/join")
    public ApiResponse<TokenResponseDto> join(@Valid @RequestBody JoinRequestDto joinRequestDto, HttpServletResponse response){

        UserResponseDto userResponseDto =  authService.join(joinRequestDto);

        addRefreshTokenCookie(response, userResponseDto);

        TokenResponseDto responseDto = TokenResponseDto
                .builder()
                .accessToken(userResponseDto.getAccessToken())
                .isNew(userResponseDto.isNew())
                .build();

        return ApiResponse.ok(responseDto);
    }

    @PostMapping("/login")
    public ApiResponse<TokenResponseDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response){

        /**
         * 1. email & password 가 존재하는지 확인
         * 2. 존재하면 addRefeshTokenCookie 호출
         *   2-2. 존재하지 않으면, 예외처리
         *
         */
        UserResponseDto userResponseDto = authService.login(loginRequestDto);

        addRefreshTokenCookie(response, userResponseDto);

        TokenResponseDto responseDto = TokenResponseDto
                .builder()
                .accessToken(userResponseDto.getAccessToken())
                .isNew(userResponseDto.isNew())
                .build();

        return ApiResponse.ok(responseDto);
    }


    private static void addRefreshTokenCookie(HttpServletResponse response, UserResponseDto result) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", result.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(14))
                .sameSite("None")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}
