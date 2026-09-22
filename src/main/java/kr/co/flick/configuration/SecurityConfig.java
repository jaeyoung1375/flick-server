package kr.co.flick.configuration;

import kr.co.flick.auth.GithubEmailOAuth2UserService;
import kr.co.flick.auth.SocialOauth2SuccessHandler;
import kr.co.flick.auth.filter.JwtAuthenticationFilter;
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
                        .requestMatchers("/public/**", "/api/v1/**", "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth -> oauth
                        .authorizationEndpoint(auth -> auth
                                .authorizationRequestResolver(
                                        authorizationRequestResolver(clientRegistrationRepository)
                                )
                        )
                        .successHandler(socialOauth2SuccessHandler)
                        .userInfoEndpoint(userInfo -> userInfo.userService(githubEmailOAuth2UserService))
                );

        return http.build();
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
