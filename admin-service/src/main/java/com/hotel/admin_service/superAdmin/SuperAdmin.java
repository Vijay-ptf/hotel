package com.hotel.admin_service.superAdmin;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuperAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long superAdminId;
    private String superAdminName;
    private String superAdminEmail;
    private String superAdminPhoneNumber;
    private String superAdminPassword;
    private String role;
    private String resetToken;
}
