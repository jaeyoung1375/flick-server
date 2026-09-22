package kr.co.motive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MotiveServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MotiveServerApplication.class, args);
	}

}
