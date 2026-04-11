package com.example.cafe_con_huellas.model.entity;

/**
 * Enumerado que define los niveles de acceso de los usuarios en el sistema.
 */
public enum Role {
    /** Acceso total al sistema: gestión de mascotas, usuarios y procesos de adopción. */
    ADMIN,
    /** Usuario estándar con permisos limitados: consulta del catálogo y solicitud de adopciones. */
    USER
}
