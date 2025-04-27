package com.app.smart_document_vault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@PropertySource("classpath:additional.properties")
public class SmartDocumentVaultApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartDocumentVaultApplication.class, args);
	}

}
