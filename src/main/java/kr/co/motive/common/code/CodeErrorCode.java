package kr.co.motive.common.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum CodeErrorCode implements ResponseCode {

	/** 공통코드를 찾을 수 없습니다. */
	CODE_NOT_FOUND("C0001", HttpStatus.NOT_FOUND, "공통코드를 찾을 수 없습니다."),
	/** 이미 등록된 공통코드입니다. */
	CODE_DUPLICATED("C0002", HttpStatus.CONFLICT, "이미 등록된 공통코드입니다."),
	/** 상세코드를 찾을 수 없습니다. */
	DETAIL_CODE_NOT_FOUND("C0003", HttpStatus.NOT_FOUND, "상세코드를 찾을 수 없습니다."),
	/** 이미 등록된 상세코드입니다. */
	DETAIL_CODE_DUPLICATED("C0004", HttpStatus.CONFLICT, "이미 등록된 상세코드입니다.");

	/** 코드 */
	private final String code;

	/** HttpStatus */
	private final HttpStatus httpStatus;

	/** 메시지 키 */
	private final String message;

}