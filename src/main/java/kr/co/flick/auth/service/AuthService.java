package kr.co.flick.auth.service;

import kr.co.flick.auth.dto.*;
import kr.co.flick.auth.enums.Role;
import kr.co.flick.auth.mapper.AuthMapper;
import kr.co.flick.auth.util.JwtTokenUtil;
import kr.co.flick.common.code.UserErrorCode;
import kr.co.flick.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthService {

    private final AuthMapper authMapper;

    private final JwtTokenUtil jwtTokenUtil;

    private final StringRedisTemplate redisTemplate;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDto join(JoinRequestDto joinRequestDto) {
        /**
         * 1. AuthMapper.findByEmail(userInserDto.getEmail())
         *  1-2 중복 email인 경우, 예외처리
         * 2. 비밀번호는 Bcrypt로 암호화 처리
         * 3. userInserDto로 AuthMapper.insertUser()
         *
         */

        boolean isExistsEmail = authMapper.existsByEmail(joinRequestDto.getEmail());
        if (isExistsEmail) {
            throw new CustomException(UserErrorCode.EMAIL_DUPLICATED); // 이미 사용중인 이메일입니다.
        }

        String encodedPassword = passwordEncoder.encode(joinRequestDto.getPassword());

        UserInsertDto userInsertDto = UserInsertDto.builder()
                .email(joinRequestDto.getEmail())
                .passwordHash(encodedPassword)
                .nickname(joinRequestDto.getNickname())
                .status("ACTIVE")
                .role(Role.USER)
                .name(joinRequestDto.getNickname())
                .build();

        authMapper.insertUser(userInsertDto);


        return issueToken(userInsertDto.getUserId(), userInsertDto.getRole(), true);

    }

    @Transactional
    public UserResponseDto login(LoginRequestDto loginRequestDto) {

        if(!StringUtils.hasText(loginRequestDto.getEmail()) || !StringUtils.hasText(loginRequestDto.getPassword())){
            throw new CustomException(UserErrorCode.INVALID_INFO);
        }

        User user = authMapper.findByEmail(loginRequestDto.getEmail());
        if (user == null  || user.getPasswordHash() == null) {
            throw new CustomException(UserErrorCode.INVALID_INFO);
        }

        boolean matches = passwordEncoder.matches(loginRequestDto.getPassword(), user.getPasswordHash());

        if (!matches) {
            throw new CustomException(UserErrorCode.INVALID_INFO);
        }

        return issueToken(user.getUserId(), user.getRole(), false);
    }


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
