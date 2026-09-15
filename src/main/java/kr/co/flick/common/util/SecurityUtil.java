package kr.co.flick.common.util;

import kr.co.flick.common.code.UserErrorCode;
import kr.co.flick.common.exception.CustomException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

	private SecurityUtil() {
		// 인스턴스화 방지
	}

	public static Long getUserId() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !(authentication.getPrincipal() instanceof Long userId)) {
			throw new CustomException(UserErrorCode.UNAUTHORIZED);
		}

		return userId;
	}
}
