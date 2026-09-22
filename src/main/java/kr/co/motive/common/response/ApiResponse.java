package kr.co.motive.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.motive.common.code.CommonErrorCode;
import kr.co.motive.common.code.ResponseCode;
import lombok.Getter;

@Getter
@Schema(description = "API 응답코드 DTO")
public class ApiResponse<T> {

	@Schema(description = "응답코드", example = "0000")
    private String code;
	private String message;
    private T data;

    private ApiResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ApiResponse() {
		// TODO Auto-generated constructor stub
	}

    /* ====== static factory ====== */

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(CommonErrorCode.OK.getCode(), CommonErrorCode.OK.getMessage(),  data);
    }

    public static <T> ApiResponse<T> of(String code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(CommonErrorCode.OK.getCode(), CommonErrorCode.OK.getMessage(), null);
    }


    /* ====== error ====== */

    public static ApiResponse<Void> error(ResponseCode code) {
        return new ApiResponse<>(
                code.getCode(),
                code.getMessage(),
                null
        );
    }

    /** 검증 오류처럼 코드는 고정이지만 메시지를 동적으로 넘겨야 할 때 사용 */
    public static ApiResponse<Void> error(ResponseCode code, String message) {
        return new ApiResponse<>(
                code.getCode(),
                message,
                null
        );
    }

}