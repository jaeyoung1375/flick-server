package kr.co.motive.auth;

import kr.co.motive.auth.dto.GithubEmailResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * GitHub는 프로필에 공개 이메일을 설정하지 않으면 /user 응답의 email이 null로 온다.
 * user:email 스코프로 검증된 이메일은 /user/emails를 별도 호출해야 얻을 수 있어,
 * DefaultOAuth2UserService 결과에 이메일이 없을 때만 이를 보강한다.
 * kakao/google 및 이메일이 이미 있는 github 사용자는 delegate 결과를 그대로 반환한다.
 */
@Component
public class GithubEmailOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    private final RestClient restClient = RestClient.create();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if (!"github".equals(registrationId) || oAuth2User.getAttribute("email") != null) {
            return oAuth2User;
        }

        String email = fetchPrimaryVerifiedEmail(userRequest.getAccessToken().getTokenValue());

        Map<String, Object> attributes = new LinkedHashMap<>(oAuth2User.getAttributes());
        attributes.put("email", email);

        return new DefaultOAuth2User(oAuth2User.getAuthorities(), attributes, "id");
    }

    private String fetchPrimaryVerifiedEmail(String accessToken) {
        List<GithubEmailResponse> emails = restClient.get()
                .uri("https://api.github.com/user/emails")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.USER_AGENT, "motive-server")
                .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .retrieve()
                .body(new ParameterizedTypeReference<List<GithubEmailResponse>>() {});

        if (emails == null) {
            return "";
        }

        return emails.stream()
                .filter(e -> e.isPrimary() && e.isVerified())
                .map(GithubEmailResponse::getEmail)
                .findFirst()
                .orElse("");
    }
}
