package kr.co.motive.common.exception;

import kr.co.motive.common.code.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import kr.co.motive.common.code.ResponseCode;
import kr.co.motive.common.response.ApiResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e){

        ResponseCode code = e.getResponseCode();
        log.warn("[CustomException] code={}, message={}", code.getCode(), code.getMessage());

        return ResponseEntity
                .status(code.getHttpStatus())
                .body(ApiResponse.error(code));
    }

    /** @Valid 검증 실패 → 첫 번째 필드 에러 메시지를 400으로 반환 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException e){

        String message = e.getBindingResult().getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse(CommonErrorCode.INVALID_INPUT.getMessage());

        log.warn("[ValidationException] message={}", message);

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(CommonErrorCode.INVALID_INPUT, message));
    }

    /** MyBatis 쿼리 실행 오류 */
    @ExceptionHandler(MyBatisSystemException.class)
    public ResponseEntity<ApiResponse<Void>> handleMyBatisException(MyBatisSystemException e){

        log.error("[MyBatisSystemException] MyBatis 쿼리 오류: {}", e.getMessage(), e);

        Throwable cause = e.getCause() != null ? e.getCause() : e;
        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.error(CommonErrorCode.INTERNAL_SERVER_ERROR,
                        "[쿼리 오류] " + cause.getMessage()));
    }

    /** DB 접근 오류 (쿼리 문법, 제약조건 위반 등) */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataAccessException(DataAccessException e){

        log.error("[DataAccessException] DB 접근 오류: {}", e.getMessage(), e);

        Throwable cause = e.getCause() != null ? e.getCause() : e;
        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.error(CommonErrorCode.INTERNAL_SERVER_ERROR,
                        "[DB 오류] " + cause.getMessage()));
    }

    /** 서비스 로직 오류 등 예상치 못한 예외 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e){

        log.error("[Exception] 처리되지 않은 예외: {}", e.getMessage(), e);

        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.error(CommonErrorCode.INTERNAL_SERVER_ERROR,
                        "[서버 오류] " + e.getClass().getSimpleName() + ": " + e.getMessage()));
    }
}