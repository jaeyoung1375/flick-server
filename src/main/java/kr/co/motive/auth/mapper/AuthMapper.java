package kr.co.motive.auth.mapper;

import kr.co.motive.auth.dto.SocialAccount;
import kr.co.motive.auth.dto.User;
import kr.co.motive.auth.dto.UserInsertDto;
import kr.co.motive.auth.enums.Provider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AuthMapper {

    /**
     * provider + providerUserId로 연동된 소셜 계정 조회
     * 연동된 계정이 없으면 null
     */
    SocialAccount findSocialAccount(@Param("provider") Provider provider,
                                    @Param("providerUserId") String providerUserId);

    /**
     * userId로 회원 조회 (ACTIVE 상태만)
     */
    User findUser(Long userId);

    /**
     * email로 회원 조회 (ACTIVE 상태만) — 소셜 최초 연동 시 기존 이메일 가입 여부 확인용
     */
    User findByEmail(String email);

    /**
     * 신규 회원 등록. insert 후 USERS_SEQ로 채번된 값이 dto.userId에 채워짐
     */
    void insertUser(UserInsertDto userInsertDto);

    /**
     * 회원에 소셜 계정 연동 정보 등록
     */
    void insertSocialAccount(@Param("userId") Long userId,
                             @Param("provider") Provider provider,
                             @Param("providerUserId") String providerUserId);

    /**
     * 최근 로그인 일시 갱신
     */
    void updateLastLoginDt(Long userId);

    /**
     * 닉네임, 성별, 생년월일 갱신 (운동프로필 등록 시 사용)
     */
    void updateProfileInfo(@Param("userId") Long userId,
                           @Param("nickname") String nickname,
                           @Param("gender") String gender,
                           @Param("birth") String birth);

    /**
     * 프로필 파일아이디 갱신 (운동프로필 등록/수정 시 이미지 첨부한 경우 사용)
     */
    void updateProfileFileId(@Param("userId") Long userId,
                             @Param("profileFileId") Long profileFileId);
}
