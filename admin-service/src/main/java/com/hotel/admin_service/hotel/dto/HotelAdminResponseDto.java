package com.hotel.admin_service.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class HotelAdminResponseDto {

    private Long adminId;
    private String adminName;
    private Long hotelId;
    private String hotelName;
    private String district;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
