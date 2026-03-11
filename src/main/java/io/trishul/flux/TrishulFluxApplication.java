package io.trishul.flux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TrishulFluxApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrishulFluxApplication.class, args);
	}

}
