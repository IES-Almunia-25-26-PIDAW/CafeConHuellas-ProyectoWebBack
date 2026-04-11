package com.example.cafe_con_huellas.dto;

import com.example.cafe_con_huellas.model.entity.AdoptionRequestStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO para el formulario de solicitud de adopción cumplimentado por el usuario.
 * <p>
 * Recoge información sobre la vivienda, convivencia, experiencia con animales
 * y motivación del solicitante. Incluye campos de contexto (nombre, email, mascota)
 * para que el administrador pueda revisar la solicitud sin consultas adicionales.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdoptionRequestDTO {

    /** Identificador único de la solicitud. Nulo en creaciones. */
    private Long id;

    /** Identificador del token de formulario vinculado a esta solicitud. */
    private Long formTokenId;

    /** Nombre completo del solicitante. Campo informativo para el administrador. */
    private String userName;

    /** Email de contacto del solicitante. Campo informativo para el administrador. */
    private String userEmail;

    /** Nombre de la mascota solicitada. Campo informativo para el administrador. */
    private String petName;

    /** Dirección postal del solicitante. */
    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 255)
    private String address;

    /** Ciudad de residencia del solicitante. */
    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 100)
    private String city;

    /** Tipo de vivienda del solicitante (ej: PISO, CASA, ADOSADO). */
    @NotBlank(message = "El tipo de vivienda es obligatorio")
    private String housingType;

    /** Indica si la vivienda dispone de jardín o zona exterior. */
    @NotNull(message = "Indica si tiene jardín")
    private Boolean hasGarden;

    /** Indica si el solicitante convive con otras mascotas. */
    @NotNull(message = "Indica si tiene otras mascotas")
    private Boolean hasOtherPets;

    /** Indica si hay menores de edad en el hogar. */
    @NotNull(message = "Indica si hay niños en casa")
    private Boolean hasChildren;

    /** Horas al día que el animal estaría solo en casa. Entre 0 y 24. */
    @NotNull(message = "Las horas solo es obligatorio")
    @Min(value = 0, message = "No puede ser negativo")
    @Max(value = 24, message = "No puede superar las 24 horas")
    private Integer hoursAlonePerDay;

    /** Indica si el solicitante tiene experiencia previa con animales. */
    @NotNull(message = "Indica si tiene experiencia con mascotas")
    private Boolean experienceWithPets;

    /** Motivo principal por el que el solicitante desea adoptar. Entre 20 y 2000 caracteres. */
    @NotBlank(message = "El motivo de adopción es obligatorio")
    @Size(min = 20, max = 2000, message = "El motivo debe tener entre 20 y 2000 caracteres")
    private String reasonForAdoption;

    /** Indica si el solicitante acepta el seguimiento post-adopción por parte del refugio. */
    @NotNull(message = "Debes aceptar o rechazar el seguimiento")
    private Boolean agreesToFollowUp;

    /** Información adicional que el solicitante quiera aportar. Opcional. */
    @Size(max = 2000)
    private String additionalInfo;

    /** Estado actual de la solicitud (PENDIENTE, APROBADA, DENEGADA). Solo lectura. */
    private AdoptionRequestStatus status;

    /** Fecha y hora en que se envió el formulario. Solo lectura. */
    private LocalDateTime submittedAt;
}