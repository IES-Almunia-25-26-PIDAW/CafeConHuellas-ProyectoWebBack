package com.example.cafe_con_huellas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación Café con Huellas.
 * Punto de entrada del servidor Spring Boot.
 */
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
