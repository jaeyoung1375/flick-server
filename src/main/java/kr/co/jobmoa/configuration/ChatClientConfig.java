package kr.co.jobmoa.configuration;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI가 자동설정한 {@link ChatClient.Builder}로 앱 전역에서 재사용할 {@link ChatClient} 빈을 만든다.
 */
@Configuration
public class ChatClientConfig {

	@Bean
	public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
		return chatClientBuilder.build();
	}

}
