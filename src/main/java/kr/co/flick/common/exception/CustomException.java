package kr.co.flick.common.exception;

import kr.co.flick.common.code.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException {

	 private final ResponseCode responseCode;
}
