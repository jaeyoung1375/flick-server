package kr.co.motive.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import kr.co.motive.auth.dto.SocialAccount;
import kr.co.motive.auth.dto.User;
import kr.co.motive.auth.dto.UserInsertDto;
import kr.co.motive.auth.dto.UserResponseDto;
import kr.co.motive.auth.enums.Role;
import kr.co.motive.auth.mapper.AuthMapper;
import kr.co.motive.auth.util.JwtTokenUtil;
import kr.co.motive.common.code.UserErrorCode;
import kr.co.motive.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthService {

    private final AuthMapper authMapper;

    private final JwtTokenUtil jwtTokenUtil;

    private final StringRedisTemplate redisTemplate;

    @Transactional
    public UserResponseDto socialLogin(SocialAccount socialAccount, String name, String email){

        SocialAccount account = authMapper.findSocialAccount(socialAccount.getProvider(), socialAccount.getProviderUserId());

        // 이미 연동된 계정인 경우
        if(account != null){
            User user = authMapper.findUser(account.getUserId());

            // 정지 혹은 탈퇴된 회원인경우
            if(user == null){
                // 사용자를 찾을 수 없습니다.
                throw new CustomException(UserErrorCode.USER_NOT_FOUND);
            }

            return issueToken(user.getUserId(), user.getRole(), false);
        }else{
            User existedUser = authMapper.findByEmail(email);

            // 다른 방식으로 가입된 회원이 연동하는 경우(예: 구글 연동된 회원이 카카로 최초 연동하는 경우)
            if(existedUser != null){
                authMapper.insertSocialAccount(
                        existedUser.getUserId(),
                        socialAccount.getProvider(),
                        socialAccount.getProviderUserId()
                );

                return issueToken(existedUser.getUserId(), existedUser.getRole(), false);
            // 완전 신규 가입인 경우
            }else{
                UserInsertDto userInsertDto = UserInsertDto
                        .builder()
                        .name(name)
                        .email(email)
                        .passwordHash(null)
                        .status("ACTIVE")
                        .role(Role.USER)
                        .build();
                authMapper.insertUser(userInsertDto);
                authMapper.insertSocialAccount(
                        userInsertDto.getUserId(),
                        socialAccount.getProvider(),
                        socialAccount.getProviderUserId());

                return issueToken(userInsertDto.getUserId(), Role.USER, true);
            }
        }
    }

    private UserResponseDto issueToken(Long userId, Role role, boolean isNew){
        authMapper.updateLastLoginDt(userId);

        String accessToken = jwtTokenUtil.createAccessToken(userId, role.name());
        String refreshToken = jwtTokenUtil.createRefreshToken(userId);
        redisTemplate.opsForValue().set("refresh:" +userId,refreshToken, Duration.ofDays(14));


        return UserResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNew(isNew)
                .build();

    }

    public UserResponseDto refresh(String refreshToken){
        Long userId = jwtTokenUtil.getUserId(refreshToken);

        String saved = redisTemplate.opsForValue().get("refresh:" +userId);

        if(saved == null || !saved.equals(refreshToken)){
            throw new CustomException(UserErrorCode.INVALID_REFRESH_TOKEN);
        }

        User user = authMapper.findUser(userId);

        return issueToken(user.getUserId(), user.getRole(), false);


    }

    public User findUser(Long userId){
        return authMapper.findUser(userId);
    }

    public void logout(Long userId){
        redisTemplate.delete("refresh:" + userId);
    }

    public UserResponseDto exchangeCode(String code){
        String key = "oauth:exchange:" + code;
        String stored = redisTemplate.opsForValue().get(key);

        if(stored == null){
            throw new CustomException(UserErrorCode.INVALID_EXCHANGE_CODE);
        }

        redisTemplate.delete(key); // 1회용

        String[] parts = stored.split("\\|", 3);
        if(parts.length != 3){
            throw new CustomException(UserErrorCode.INVALID_EXCHANGE_CODE);
        }

        return UserResponseDto.builder()
                .accessToken(parts[0])
                .refreshToken(parts[1])
                .isNew(Boolean.parseBoolean(parts[2]))
                .build();
    }


}
