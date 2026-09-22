package kr.co.flick;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FlickServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlickServerApplication.class, args);
	}

}
