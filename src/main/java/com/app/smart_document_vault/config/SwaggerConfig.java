package com.app.smart_document_vault.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@Configuration
@OpenAPIDefinition(
		info = @Info(
				contact = @Contact(
						name="Vasanth",
						email="mvasanthakumar381@gmail.com"
						),
				description = "Documentation for Smart Document Vault",
				title = "SMART DOCUMENT VAULT",
				version = "1.0"
				),
		security = {
				@SecurityRequirement(
						name = "BearerAuth"
				)
		}
)

@SecurityScheme(
		name = "BearerAuth",
		description = "JWT Auth description",
		scheme = "bearer",
		type = SecuritySchemeType.HTTP,
		bearerFormat = "JWT",
		in = SecuritySchemeIn.HEADER
)

public class SwaggerConfig {

}
