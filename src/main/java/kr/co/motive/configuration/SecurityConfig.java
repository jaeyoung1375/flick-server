package kr.co.motive.configuration;

import kr.co.motive.auth.GithubEmailOAuth2UserService;
import kr.co.motive.auth.MobileAwareOAuth2AuthorizationRequestResolver;
import kr.co.motive.auth.SocialOauth2SuccessHandler;
import kr.co.motive.auth.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsConfig corsConfig;
    private final SocialOauth2SuccessHandler socialOauth2SuccessHandler;
    private final GithubEmailOAuth2UserService githubEmailOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           ClientRegistrationRepository clientRegistrationRepository, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**", "/api/v1/**", "/swagger-ui/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth -> oauth
                        .authorizationEndpoint(auth -> auth
                                .authorizationRequestResolver(
                                        mobileAwareAuthorizationRequestResolver(clientRegistrationRepository)
                                )
                        )
                        .successHandler(socialOauth2SuccessHandler)
                        .userInfoEndpoint(userInfo -> userInfo.userService(githubEmailOAuth2UserService))
                );

        return http.build();
    }

    /**
     * 모바일 앱(?platform=mobile) 요청을 세션에 마킹해서 SocialOauth2SuccessHandler가 리다이렉트 방식을 분기하게 한다.
     */
    @Bean
    public OAuth2AuthorizationRequestResolver mobileAwareAuthorizationRequestResolver(
            ClientRegistrationRepository repo) {
        return new MobileAwareOAuth2AuthorizationRequestResolver(authorizationRequestResolver(repo));
    }

    /**
     * 카카오는 PKCE 미지원 → code_challenge/code_challenge_method 파라미터 제거
     */
    @Bean
    public OAuth2AuthorizationRequestResolver authorizationRequestResolver(
            ClientRegistrationRepository repo) {

        DefaultOAuth2AuthorizationRequestResolver resolver =
                new DefaultOAuth2AuthorizationRequestResolver(repo, "/oauth2/authorization");

        resolver.setAuthorizationRequestCustomizer(builder -> {
            final String[] registrationId = {null};
            builder.attributes(attrs -> {
                registrationId[0] = (String) attrs.get("registration_id");
                if ("kakao".equals(registrationId[0])) {
                    attrs.remove("code_challenge_method");
                }
            });
            if ("kakao".equals(registrationId[0])) {
                builder.additionalParameters(params -> {
                    params.remove("code_challenge");
                    params.remove("code_challenge_method");
                });
            }
        });

        return resolver;
    }
}
