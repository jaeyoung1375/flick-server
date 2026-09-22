package kr.co.motive.common.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum CommonErrorCode implements ResponseCode {

	/** 정상 처리되었습니다. */
	OK("0000", HttpStatus.OK, "정상 처리되었습니다." ),
	/** 조회 데이터가 존재하지 않습니다. */
	DATA_NOT_FOUND("0001", HttpStatus.OK, "조회 데이터가 존재하지 않습니다."),
	/** 입력값이 올바르지 않습니다. */
	INVALID_INPUT("V0001", HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
	/** 이미 지원한 게시글입니다. */
	DUPLICATE_APPLY("A0001", HttpStatus.BAD_REQUEST, "이미 지원한 게시글입니다."),
	/** 접근 권한이 없습니다. */
	ACCESS_DENIED("F0001", HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
	/** 서버 오류가 발생했습니다. */
	INTERNAL_SERVER_ERROR("9999", HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
	/** 상태코드가 존재하지 않습니다 */
	INVALID_STATUS_CD("V0002", HttpStatus.BAD_REQUEST, "상태코드가 존재하지 않습니다.");



	/** 코드 */
    private final String code;

    /** HttpStatus */
    private final HttpStatus httpStatus;

    /** 메시지 키 */
    private final String message;





}
