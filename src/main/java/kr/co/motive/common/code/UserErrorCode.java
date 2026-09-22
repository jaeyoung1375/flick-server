package kr.co.motive.common.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum UserErrorCode implements ResponseCode {

	/** 이메일 또는 비밀번호가 올바르지 않습니다. */
	INVALID_INFO("U0001", HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다." ),
	/** 비밀번호가 올바르지 않습니다. */
	INVALID_PASSWORD("U0002", HttpStatus.UNAUTHORIZED, "비밀번호가 올바르지 않습니다."),
	/** 유효하지 않은 refresh token 입니다. */
	INVALID_REFRESH_TOKEN("U0003", HttpStatus.UNAUTHORIZED, "유효하지 않은 refresh token 입니다."),
	/** 로그아웃된 토큰입니다. */
	INVALID_LOGOUT_TOKEN("U0004", HttpStatus.UNAUTHORIZED, "로그아웃된 토큰입니다."),
    /** 사용자를 찾을 수 없습니다. */
    USER_NOT_FOUND("U0005", HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    /** 이미 사용중인 이메일입니다. */
    EMAIL_DUPLICATED("U0006", HttpStatus.CONFLICT, "이미 사용중인 이메일입니다."),
    /** 탈퇴한 회원입니다. */
    USER_WITHDRAWN("U0007", HttpStatus.FORBIDDEN, "탈퇴한 회원입니다."),
    /** 정지된 회원입니다. */
    USER_BLOCKED("U0008", HttpStatus.FORBIDDEN, "정지된 회원입니다."),
    /** 소셜 로그인 사용자는 비밀번호 변경 불가 */
    SOCIAL_PW_BLOCKED("U0009", HttpStatus.FORBIDDEN, "소셜 로그인 사용자는 비밀번호 변경 불가입니다."),
    /** 소셜 토큰 만료 -> 재로그인 필요 */
    SOCIAL_TOKEN_EXPIRED("U0010", HttpStatus.FORBIDDEN, "소셜 토큰 만료 → 재로그인 필요"),
    /** 연동된 소셜 계정 정보 없음 **/
    SOCIAL_ACCOUNT_NOT_FOUND("U0011", HttpStatus.NOT_FOUND, "연동된 소셜 계정 정보가 없습니다."),
    /** 활성 상태인 회원만 수정 가능 **/
    USER_NOT_ACTIVE("U0012", HttpStatus.BAD_REQUEST, "활성 상태인 회원만 수정할 수 있습니다."),
    /** 인증이 필요합니다. */
    UNAUTHORIZED("U0013", HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    /** 유효하지 않거나 만료된 모바일 로그인 교환 코드입니다. */
    INVALID_EXCHANGE_CODE("U0014", HttpStatus.UNAUTHORIZED, "유효하지 않거나 만료된 인증 코드입니다.");


	/** 코드 */
    private final String code;

    /** HttpStatus */
    private final HttpStatus httpStatus;

    /** 메시지 키 */
    private final String message;

}
