package kr.co.flick.configuration;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	@Value("${file.upload.path}")
	private String uploadPath;

	@Value("${file.upload.server}")
	private String uploadServer;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // addPathPrefix는 매치되는 첫 규칙 하나만 적용하므로(누적 아님),
        // "/api/v1" + "/public"이 둘 다 필요한 경우를 더 구체적인 규칙으로 먼저 등록한다.
        configurer.addPathPrefix("/api/v1/public",
            c -> c.isAnnotationPresent(RestController.class)
            && c.isAnnotationPresent(PublicController.class));

        configurer.addPathPrefix("/api/v1",
            c -> c.isAnnotationPresent(RestController.class)
            && !c.getPackage().getName().startsWith("org.springdoc"));
    }


	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		registry
			.addResourceHandler(uploadServer + "/**") // 브라우저에서 접근할 URL
			.addResourceLocations("file:"+uploadPath + "/"); // 실제 서버 파일 경로
	}


}
