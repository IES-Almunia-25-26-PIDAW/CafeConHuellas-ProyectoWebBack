package com.example.cafe_con_huellas.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

// DTO para la transferencia de datos de eventos del refugio
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDTO {

    // Identificador único del evento
    private Long id;

    // Nombre del evento con validación de presencia y longitud
    @NotBlank(message = "El nombre del evento es obligatorio")
    @Size(min = 5, max = 100, message = "El nombre debe tener entre 5 y 100 caracteres")
    private String name;

    // Descripción detallada obligatoria
    @NotBlank(message = "La descripción del evento es obligatoria")
    @Size(min = 20, message = "La descripción debe ser detallada (mínimo 20 caracteres)")
    private String description;

    // Fecha del evento: validamos que no sea una fecha pasada
    @NotNull(message = "La fecha del evento es obligatoria")
    @Future(message = "La fecha del evento debe ser una fecha futura")
    private LocalDateTime eventDate;

    // Lugar de realización del evento
    @NotBlank(message = "La ubicación es obligatoria")
    @Size(max = 255, message = "La ubicación no puede superar los 255 caracteres")
    private String location;

    // URL de la imagen (opcional, pero validamos formato si se envía)
    @Pattern(regexp = "^https?://.*", message = "La imagen debe ser una URL válida (http/https)")
    private String imageUrl;

    // Tipo de evento. Validamos contra las opciones del ENUM
    @NotBlank(message = "El tipo de evento es obligatorio")
    @Pattern(regexp = "^(ADOPCION|MERCADILLO|EDUCACION|RECAUDACION|OTRO)$",
            message = "Tipo de evento no válido. Use: ADOPCION, MERCADILLO, EDUCACION, RECAUDACION u OTRO")
    private String eventType;

    // Estado del evento. Validamos contra las opciones del ENUM
    @NotBlank(message = "El estado del evento es obligatorio")
    @Pattern(regexp = "^(PROGRAMADO|EN_CURSO|FINALIZADO|CANCELADO)$",
            message = "Estado no válido. Use: PROGRAMADO, EN_CURSO, FINALIZADO o CANCELADO")
    private String status;

    // Capacidad máxima de asistentes (opcional)
    @Positive(message = "La capacidad máxima debe ser un número positivo")
    @Max(value = 10000, message = "Para eventos de más de 10.000 personas, contacte con soporte")
    private Integer maxCapacity;

    // Fecha de creación (lectura para el Front)
    private LocalDateTime createdAt;

}
