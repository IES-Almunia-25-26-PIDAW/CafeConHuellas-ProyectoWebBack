package com.example.cafe_con_huellas.model.entity;

/**
 * Enumerado que clasifica el tipo de donación recibida por el refugio.
 */
public enum DonationCategory {
    /** Donación en dinero. */
    MONETARIA,
    /** Donación de comida para los animales. */
    ALIMENTACION,
    /** Donación de material (mantas, jaulas, etc.). */
    MATERIAL,
    /** Donación de juguetes para los animales. */
    JUGUETES,
    /** Donación de medicamentos o productos veterinarios. */
    MEDICAMENTOS,
    /** Donación recurrente mediante suscripción. */
    SUSCRIPCION,
    /** Otros tipos de donación no categorizados. */
    OTROS
}
