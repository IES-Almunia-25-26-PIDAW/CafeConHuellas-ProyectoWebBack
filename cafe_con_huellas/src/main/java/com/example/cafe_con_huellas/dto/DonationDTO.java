package com.example.cafe_con_huellas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// DTO para donaciones
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonationDTO {
    private Long id;
    private Long userId;
    private LocalDateTime date;
    private String type;
    private Double amount;
}
