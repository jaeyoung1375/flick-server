package kr.co.motive.common.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public enum ExerciseErrorCode implements ResponseCode {

	/** 운동을 찾을 수 없습니다. */
	EXERCISE_NOT_FOUND("EX0001", HttpStatus.NOT_FOUND, "운동을 찾을 수 없습니다.");

	/** 코드 */
	private final String code;

	/** HttpStatus */
	private final HttpStatus httpStatus;

	/** 메시지 키 */
	private final String message;

}
