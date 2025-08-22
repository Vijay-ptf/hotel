package com.hotel.admin_service.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminDto {

    private Long adminId;
    private String adminName;
    private String adminEmail;
    private String adminPhoneNumber;
}
