package kr.co.motive.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ClientIpProvider {

    private static final String UNKNOWN = "unknown";
    private static final String LOCAL_IPV6 = "0:0:0:0:0:0:0:1";
    private static final String LOCAL_IPV4 = "127.0.0.1";

    public String getClientIp(HttpServletRequest request) {
        String ip = firstForwardedIp(request.getHeader("X-Forwarded-For"));

        if (!StringUtils.hasText(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (!StringUtils.hasText(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (LOCAL_IPV6.equals(ip)) {
            return LOCAL_IPV4;
        }

        return ip;
    }

    private String firstForwardedIp(String forwardedFor) {
        if (!StringUtils.hasText(forwardedFor)) {
            return null;
        }

        String firstIp = forwardedFor.split(",")[0].trim();
        if (!StringUtils.hasText(firstIp) || UNKNOWN.equalsIgnoreCase(firstIp)) {
            return null;
        }

        return firstIp;
    }
}
