package com.example.cafe_con_huellas.model.entity;

/**
 * Enumerado que representa el estado del proceso de adopción de una mascota.
 */
public enum AdoptionStatus {
    /** La mascota está disponible para ser adoptada. */
    NO_ADOPTADO,
    /** La mascota tiene un proceso de adopción en curso. */
    EN_PROCESO,
    /** La mascota ya ha sido adoptada. */
    ADOPTADO
}