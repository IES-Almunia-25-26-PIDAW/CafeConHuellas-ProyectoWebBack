package com.example.cafe_con_huellas.dto;

import com.example.cafe_con_huellas.model.entity.AdoptionRequestStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdoptionRequestDTO {

    private Long id;

    // ID del token vinculado, lo usamos para leer usuario y mascota desde el backend
    private Long formTokenId;

    // Datos de contexto para que el admin los vea sin hacer joins en el frontend
    private String userName;
    private String userEmail;
    private String petName;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 255)
    private String address;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 100)
    private String city;

    @NotBlank(message = "El tipo de vivienda es obligatorio")
    private String housingType;

    @NotNull(message = "Indica si tiene jardín")
    private Boolean hasGarden;

    @NotNull(message = "Indica si tiene otras mascotas")
    private Boolean hasOtherPets;

    @NotNull(message = "Indica si hay niños en casa")
    private Boolean hasChildren;

    @NotNull(message = "Las horas solo es obligatorio")
    @Min(value = 0, message = "No puede ser negativo")
    @Max(value = 24, message = "No puede superar las 24 horas")
    private Integer hoursAlonePerDay;

    @NotNull(message = "Indica si tiene experiencia con mascotas")
    private Boolean experienceWithPets;

    @NotBlank(message = "El motivo de adopción es obligatorio")
    @Size(min = 20, max = 2000, message = "El motivo debe tener entre 20 y 2000 caracteres")
    private String reasonForAdoption;

    @NotNull(message = "Debes aceptar o rechazar el seguimiento")
    private Boolean agreesToFollowUp;

    @Size(max = 2000)
    private String additionalInfo;

    private AdoptionRequestStatus status;
    private LocalDateTime submittedAt;
}