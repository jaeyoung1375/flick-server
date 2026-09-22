package kr.co.flick.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 인증 없이 접근 가능한 공개 API 컨트롤러 표시.
 * WebConfig가 이 어노테이션이 붙은 클래스에 "/public" 경로 프리픽스를 자동으로 붙인다.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PublicController {
}
