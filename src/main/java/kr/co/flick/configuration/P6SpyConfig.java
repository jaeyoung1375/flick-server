package kr.co.flick.configuration;

import com.p6spy.engine.spy.P6SpyOptions;
import org.springframework.context.annotation.Configuration;

@Configuration
public class P6SpyConfig {

    public P6SpyConfig() {
        // Formatter 지정
        P6SpyOptions.getActiveInstance().setLogMessageFormat(kr.co.flick.configuration.P6spyPrettySqlFormatter.class.getName());
        // Appender 지정
        P6SpyOptions.getActiveInstance().setAppender(kr.co.flick.configuration.CustomP6SpyLogger.class.getName());
    }
}