package com.example.cafe_con_huellas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO completo para detalles de usuario
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailDTO {
    private Long id;
    private String firstName;
    private String lastName1;
    private String lastName2;
    private String email;
    private String phone;
    private String role;
    private String imageUrl;
}
