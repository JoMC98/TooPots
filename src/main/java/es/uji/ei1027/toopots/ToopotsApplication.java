package es.uji.ei1027.toopots;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ToopotsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ToopotsApplication.class, args);
	}

}
