package com.example.JavaBank;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "financial system",
				description = "Backend Rest APIs for Bank",
				version = "v1.0",
				contact = @Contact(
						name = "Yousef Jaber",
						email = "yousefjaber@gmail.com",
						url = "http://localhost:8080/swagger-ui/index.html#/"
				),
				license = @License(
						name = "Java Bank App",
						url = "http://localhost:8080/swagger-ui/index.html#/"
				)

		),
		externalDocs = @ExternalDocumentation(
				description = "Java Bank App",
				url = "http://localhost:8080/swagger-ui/index.html#/"
		)
)
public class JavaBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaBankApplication.class, args);
	}

}
