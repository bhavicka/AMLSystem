package com.tss.AmlSystem;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AmlSystemApplication {
	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();

		System.setProperty("DB_URL", dotenv.get("DB_URL"));
		System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

		System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
		System.setProperty("JWT_EXPIRATION_MS", dotenv.get("JWT_EXPIRATION_MS"));
		System.setProperty("JWT_REFRESH_EXPIRATION_MS", dotenv.get("JWT_REFRESH_EXPIRATION_MS"));
		SpringApplication.run(AmlSystemApplication.class, args);
	}

}
