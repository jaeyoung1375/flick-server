package kr.co.motive.configuration;

import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisConfig {

    @Bean
    public P6SpyMyBatisInterceptor p6SpyInterceptor() {
        return new P6SpyMyBatisInterceptor();
    }

    @Bean
    public ConfigurationCustomizer configurationCustomizer(P6SpyMyBatisInterceptor interceptor) {
        return configuration -> configuration.addInterceptor(interceptor);
    }
}
