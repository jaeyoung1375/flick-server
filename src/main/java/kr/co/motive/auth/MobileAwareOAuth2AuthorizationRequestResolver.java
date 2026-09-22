package kr.co.motive.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

/**
 * 모바일 앱(?platform=mobile)이 시작한 OAuth 요청인지 세션에 마킹해두고, 실제 resolve는 delegate에 위임한다.
 * SocialOauth2SuccessHandler가 이 마킹을 읽어 웹/모바일 리다이렉트 방식을 분기한다.
 */
public class MobileAwareOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    public static final String MOBILE_SESSION_ATTR = "OAUTH2_MOBILE_LOGIN";

    private final OAuth2AuthorizationRequestResolver delegate;

    public MobileAwareOAuth2AuthorizationRequestResolver(OAuth2AuthorizationRequestResolver delegate) {
        this.delegate = delegate;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest result = delegate.resolve(request);
        markIfMobile(request, result);
        return result;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest result = delegate.resolve(request, clientRegistrationId);
        markIfMobile(request, result);
        return result;
    }

    private void markIfMobile(HttpServletRequest request, OAuth2AuthorizationRequest resolved) {
        if (resolved != null && "mobile".equals(request.getParameter("platform"))) {
            request.getSession(true).setAttribute(MOBILE_SESSION_ATTR, Boolean.TRUE);
        }
    }
}
