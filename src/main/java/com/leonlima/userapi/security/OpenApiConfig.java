package com.leonlima.userapi.security;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configura o Swagger UI com suporte a autenticação JWT.
 * Após fazer login, cole o token no botão "Authorize" para testar os endpoints protegidos.
 *
 * Documentação disponível em: http://localhost:8080/swagger-ui.html
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "User Profile API",
        version = "1.0.0",
        description = "API REST para gerenciamento de perfis de usuários com autenticação JWT stateless.",
        contact = @Contact(
            name = "Leon Nogueira Lima",
            email = "leonlimask@gmail.com",
            url = "https://github.com/leonlimask20-dot"
        )
    )
)
// Define o esquema de segurança Bearer JWT usado nos endpoints protegidos
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
