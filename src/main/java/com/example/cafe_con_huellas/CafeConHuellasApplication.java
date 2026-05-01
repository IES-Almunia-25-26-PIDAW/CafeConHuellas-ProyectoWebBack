package com.example.cafe_con_huellas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Clase principal de la aplicación Café con Huellas.
 * Punto de entrada del servidor Spring Boot.
 * <p>
 * {@code @EnableScheduling} activa el soporte para tareas programadas,
 * necesario para la desactivación automática de vínculos usuario-mascota con fecha fin vencida.
 * </p>
 */
@EnableScheduling
@SpringBootApplication
public class CafeConHuellasApplication {
    /**
     * Método principal que arranca la aplicación.
     * @param args argumentos de línea de comandos
     */
	public static void main(String[] args) {
		SpringApplication.run(CafeConHuellasApplication.class, args);
	}

}
