package com.example.cafe_con_huellas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para recibir los datos del formulario de contacto público.
 * Contiene validaciones para evitar datos vacíos o excesivamente largos.
 */
@Getter
@Setter
public class ContactRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    @Size(max = 254, message = "El email no puede superar los 254 caracteres")
    private String email;

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(max = 1000, message = "El mensaje no puede superar los 1000 caracteres")
    private String mensaje;
}