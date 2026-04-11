package com.example.cafe_con_huellas.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de la documentación OpenAPI (Swagger) con soporte JWT global.
 * <p>
 * Define el esquema de autenticación Bearer para que Swagger UI
 * incluya automáticamente el token JWT en todas las peticiones de prueba.
 * La documentación es accesible en {@code /swagger-ui.html}.
 * </p>
 */
@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {

    /**
     * Configura la instancia de OpenAPI con la información del proyecto
     * y aplica el esquema de seguridad JWT a todos los endpoints de forma global.
     *
     * @return instancia de {@link OpenAPI} con los metadatos y la seguridad configurados
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // Añadimos el esquema de seguridad a TODOS los endpoints automáticamente
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearerAuth");

        return new OpenAPI()
                .info(new Info()
                        .title("Café con Huellas API")
                        .version("1.0")
                        .description("API REST para la gestión del refugio de animales")
                )
                // Esto hace que Swagger envíe el token en todas las peticiones
                .addSecurityItem(securityRequirement);
    }
}