package com.example.cafe_con_huellas.model.entity;

/**
 * Enumerado que representa el estado actual de un evento del refugio.
 */
public enum EventStatus {
    /** El evento está planificado pero aún no ha comenzado. */
    PROGRAMADO,
    /** El evento se está celebrando en este momento. */
    EN_CURSO,
    /** El evento ha concluido. */
    FINALIZADO,
    /** El evento fue cancelado y no se celebrará. */
    CANCELADO
}
