package com.example.cafe_con_huellas.model.entity;

// Define los niveles de acceso o permisos que puede tener un usuario en el sistema
// ADMIN: Tiene control total sobre el sistema (gestión de mascotas, usuarios y adopciones)
// USER: Usuario estándar con permisos limitados (ver mascotas y solicitar adopciones)
public enum Role {
    ADMIN, USER
}
