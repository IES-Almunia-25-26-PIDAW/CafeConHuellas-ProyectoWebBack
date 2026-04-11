package com.example.cafe_con_huellas.model.entity;

/**
 * Enumerado que representa el método de pago o entrega utilizado en una donación.
 */
public enum DonationMethod {
    /** Pago en metálico. */
    EFECTIVO,
    /** Transferencia bancaria. */
    TRANSFERENCIA,
    /** Pago con tarjeta de crédito o débito. */
    TARJETA,
    /** Pago mediante la aplicación Bizum. */
    BIZUM,
    /** Entrega de bienes físicos (material, alimentos, etc.). */
    ESPECIE
}
