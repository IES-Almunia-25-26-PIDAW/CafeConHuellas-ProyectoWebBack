package com.example.cafe_con_huellas.model.entity;

/**
 * Enumerado que clasifica el tipo de vínculo entre un usuario y una mascota.
 */
public enum RelationshipType {
    /** El usuario ha adoptado la mascota de forma permanente. */
    ADOPCION,
    /** El usuario acoge temporalmente a la mascota en su hogar. */
    ACOGIDA,
    /** El usuario realiza paseos con la mascota como voluntario. */
    PASEO,
    /** El usuario colabora con el refugio en tareas de voluntariado. */
    VOLUNTARIADO
}
