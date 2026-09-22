package kr.co.motive.common.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum FitnessErrorCode implements ResponseCode {

	/** 운동프로필을 찾을 수 없습니다. */
	PROFILE_NOT_FOUND("FT0001", HttpStatus.NOT_FOUND, "운동프로필을 찾을 수 없습니다."),
	/** 이미 등록된 운동프로필입니다. */
	PROFILE_ALREADY_EXISTS("FT0002", HttpStatus.CONFLICT, "이미 등록된 운동프로필입니다.");

	/** 코드 */
	private final String code;

	/** HttpStatus */
	private final HttpStatus httpStatus;

	/** 메시지 키 */
	private final String message;

}