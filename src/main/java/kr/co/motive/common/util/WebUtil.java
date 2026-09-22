package kr.co.motive.common.util;

import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 웹 요청(HttpServletRequest) 관련 유틸리티
 */
public class WebUtil {

	/** 모바일 기기(Android, iPhone 등) 판별용 패턴 */
	private static final Pattern MOBILE_PATTERN = Pattern.compile(
			"(?i)Mobile|Android|iPhone|iPod|BlackBerry|Windows Phone|Opera Mini|IEMobile"
	);

	/** 태블릿 판별용 패턴 (필요 시 모바일과 별도로 구분) */
	private static final Pattern TABLET_PATTERN = Pattern.compile(
			"(?i)iPad|Android(?!.*Mobile)|Tablet"
	);

	/** 사설(로컬/내부망) IP 판별용 패턴 */
	private static final Pattern PRIVATE_IP_PATTERN = Pattern.compile(
			"^(127\\.|10\\.|172\\.(1[6-9]|2[0-9]|3[0-1])\\.|192\\.168\\.|0:0:0:0:0:0:0:1|::1)"
	);

	private WebUtil() {
		// 인스턴스화 방지
	}

	/**
	 * 앱(In-App / 네이티브 앱)을 통한 접속 여부 확인
	 * 앱단에서 User-Agent에 커스텀 식별자(예: "MyAppName/1.0")를 추가해 전달하는 것을 전제로 함
	 * @param request HttpServletRequest
	 * @param appIdentifier User-Agent에 포함된 앱 식별 문자열 (예: "MyApp")
	 * @return 앱을 통한 접속이면 true
	 */
	public static boolean isApp(HttpServletRequest request, String appIdentifier) {
		if (request == null || appIdentifier == null || appIdentifier.isEmpty()) {
			return false;
		}
		String userAgent = request.getHeader("User-Agent");
		return userAgent != null && userAgent.contains(appIdentifier);
	}

	/**
	 * 모바일 웹(브라우저 기반) 접속 여부 확인
	 * User-Agent 헤더를 기반으로 모바일 기기 여부를 판별
	 * @param request HttpServletRequest
	 * @return 모바일 기기로 판단되면 true
	 */
	public static boolean isMobile(HttpServletRequest request) {
		if (request == null) {
			return false;
		}
		String userAgent = request.getHeader("User-Agent");
		if (userAgent == null || userAgent.isEmpty()) {
			return false;
		}
		return MOBILE_PATTERN.matcher(userAgent).find();
	}

	/**
	 * 태블릿 기기 접속 여부 확인
	 * @param request HttpServletRequest
	 * @return 태블릿 기기로 판단되면 true
	 */
	public static boolean isTablet(HttpServletRequest request) {
		if (request == null) {
			return false;
		}
		String userAgent = request.getHeader("User-Agent");
		if (userAgent == null || userAgent.isEmpty()) {
			return false;
		}
		return TABLET_PATTERN.matcher(userAgent).find();
	}

	/**
	 * 클라이언트 접속 IP 조회
	 * 프록시/로드밸런서 환경을 고려해 X-Forwarded-For 등 헤더를 우선 확인하고,
	 * 없을 경우 request.getRemoteAddr()를 반환
	 * @param request HttpServletRequest
	 * @return 클라이언트 IP 문자열 (알 수 없으면 null)
	 */
	public static String getClientIp(HttpServletRequest request) {
		if (request == null) {
			return null;
		}

		String[] headerCandidates = {
				"X-Forwarded-For",
				"Proxy-Client-IP",
				"WL-Proxy-Client-IP",
				"HTTP_X_FORWARDED_FOR",
				"HTTP_CLIENT_IP",
				"X-Real-IP"
		};

		for (String header : headerCandidates) {
			String ip = request.getHeader(header);
			if (isValidIp(ip)) {
				// X-Forwarded-For는 "client, proxy1, proxy2" 형태일 수 있어 첫 번째 값 사용
				return ip.split(",")[0].trim();
			}
		}

		return request.getRemoteAddr();
	}

	/**
	 * 헤더 값이 유효한 IP 문자열인지 확인 (null, 빈값, "unknown" 등 제외)
	 * @param ip 확인할 IP 문자열
	 * @return 유효하면 true
	 */
	private static boolean isValidIp(String ip) {
		return ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip);
	}

	/**
	 * 클라이언트 IP가 사설(내부망/로컬) IP인지 확인
	 * @param request HttpServletRequest
	 * @return 사설 IP이면 true
	 */
	public static boolean isPrivateIp(HttpServletRequest request) {
		String ip = getClientIp(request);
		return ip != null && PRIVATE_IP_PATTERN.matcher(ip).find();
	}
}