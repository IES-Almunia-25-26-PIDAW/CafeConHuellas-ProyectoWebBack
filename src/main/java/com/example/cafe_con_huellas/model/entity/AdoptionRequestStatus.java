package com.example.cafe_con_huellas.model.entity;

/**
 * Enumerado que representa los posibles estados de una solicitud de adopción.
 */
public enum AdoptionRequestStatus {
    /** La solicitud ha sido recibida y está pendiente de revisión por el administrador. */
    PENDIENTE,
    /** El administrador ha aprobado la solicitud. */
    APROBADA,
    /** El administrador ha rechazado la solicitud. */
    DENEGADA
}

