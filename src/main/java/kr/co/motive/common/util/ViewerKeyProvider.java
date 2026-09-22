//package kr.co.motive.common.util;
//
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import org.apache.commons.lang3.ObjectUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//@Component
//@RequiredArgsConstructor
//public class ViewerKeyProvider {
////    private final JwtTokenUtil jwtTokenUtil;
//
//
//    /**
//     * 조회수 redis 키 조회 (회원인 경우 회원번호, 비회원인경우 아이피를 반환한다)
//     * 회원인 경우 user:85
//     * 비회원인 경우 ip:127.0.0.1
//     * @return 조회 redis 키
//     */
//    public String getViewerKey(){
//        Long userId = getCurrentUserId();
//
//        if(!ObjectUtils.isEmpty(userId)){
//            return "user:" + userId;
//        }
//
//        HttpServletRequest request = getCurrentRequest();
//        String clientIp = WebUtil.getClientIp(request);
//
//        if(StringUtils.isBlank(clientIp)){
//            return "ip:unknown";
//        }
//
//        return "ip:" + clientIp;
//    }
//
//    /**
//     * 현재 사용자의 아이디를 반환한다.
//     * @return 사용자 아이디
//     */
//    private Long getCurrentUserId(){
//        try {
//            return jwtTokenUtil.getMemberIdFromSecurityContext();
//        }catch (Exception e){
//            return null;
//        }
//    }
//
//    /**
//     * 현재 요청을 반환한다.
//     * @return 현재 요청
//     */
//    private HttpServletRequest getCurrentRequest(){
//        ServletRequestAttributes attributes =
//                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//
//        if(attributes == null){
//            return null;
//        }
//
//        return attributes.getRequest();
//    }
//
//}
