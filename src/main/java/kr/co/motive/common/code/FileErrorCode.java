package kr.co.motive.common.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum FileErrorCode implements ResponseCode {

	FILE_EMPTY("0002", HttpStatus.OK, "파일이 없습니다."),
	FILE_UPLOAD_FAIL("0003", HttpStatus.BAD_REQUEST, "파일 저장 실패");



	/** 코드 */
    private final String code;

    /** HttpStatus */
    private final HttpStatus httpStatus;

    /** 메시지 키 */
    private final String message;





}
