package com.example.cafe_con_huellas.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de recursos web de la aplicación.
 * <p>
 * Registra la carpeta {@code uploads/} del directorio de ejecución
 * como recurso estático accesible por HTTP, permitiendo que las imágenes
 * subidas por los usuarios se sirvan directamente desde el backend.
 * </p>
 * <p>
 * Ejemplo: un archivo guardado en {@code uploads/avatars/abc.jpg}
 * será accesible en {@code http://localhost:8087/uploads/avatars/abc.jpg}.
 * </p>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Mapea las peticiones a {@code /uploads/**} al directorio físico
     * {@code uploads/} del servidor, sirviendo los archivos como recursos estáticos.
     *
     * @param registry registro de manejadores de recursos de Spring MVC
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}