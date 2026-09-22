package kr.co.flick.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.flick.auth.dto.GithubUserResponse;
import kr.co.flick.auth.dto.GoogleUserResponse;
import kr.co.flick.auth.dto.KakaoUserResponse;
import kr.co.flick.auth.dto.SocialAccount;
import kr.co.flick.auth.dto.UserResponseDto;
import kr.co.flick.auth.enums.Provider;
import kr.co.flick.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;


@Component
@RequiredArgsConstructor
@Slf4j
public class SocialOauth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;

    private final StringRedisTemplate redisTemplate;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.mobile-redirect-scheme}")
    private String mobileRedirectScheme;

    @Value("${jwt.refresh-exp-ms}")
    private long refreshExpMs;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = oauthToken.getAuthorizedClientRegistrationId();

        Provider provider = Provider.fromRegistrationId(registrationId);
        OAuth2User user = oauthToken.getPrincipal();

        String providerUserId = "";
        String email = "";
        String name = " ";



        switch (provider) {
            case KAKAO -> {
                KakaoUserResponse kakaoUser = new ObjectMapper().convertValue(user.getAttributes(), KakaoUserResponse.class);
                providerUserId = kakaoUser.getId().toString();
                email = kakaoUser.getKakaoAccount().getEmail();
                name = kakaoUser.getKakaoAccount().getProfile().getNickname();

            }
            case GOOGLE -> {
                GoogleUserResponse googleUser = new ObjectMapper().convertValue(user.getAttributes(), GoogleUserResponse.class);
                providerUserId = googleUser.getSub();
                email = googleUser.getEmail();
                name = googleUser.getName();
            }
            case GITHUB -> {
                GithubUserResponse githubUser = new ObjectMapper().convertValue(user.getAttributes(), GithubUserResponse.class);
                providerUserId = githubUser.getId().toString();
                email = githubUser.getEmail();
                name = githubUser.getName() != null ? githubUser.getName() : githubUser.getLogin();
            }
        }

        SocialAccount socialAccount = SocialAccount
                .builder()
                .provider(provider)
                .providerUserId(providerUserId)
                .build();

        UserResponseDto result = authService.socialLogin(socialAccount, name, email);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", result.getRefreshToken())
                .httpOnly(true) // JS에서 document.cookie로 못 읽음 (XSS 방어)
                .secure(false)  // HTTPS에서만 전송 (local이면 false로)
                .path("/")      // 어느 경로 요청이든 쿠키 첨부
                .maxAge(Duration.ofMillis(refreshExpMs))
                .sameSite("Lax") // 소셜 로그인 리다이렉트(다른 origin→우리 도메인)라 Lax 정도가 무난
                .build();

        response.addHeader("Set-Cookie", refreshCookie.toString());

        String redirectUrl = frontendUrl + "/";
        response.sendRedirect(redirectUrl);
    }
}
