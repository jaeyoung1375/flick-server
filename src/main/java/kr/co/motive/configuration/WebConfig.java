package kr.co.motive.configuration;


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
