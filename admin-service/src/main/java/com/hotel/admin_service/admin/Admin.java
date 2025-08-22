package com.hotel.admin_service.admin;

import com.hotel.admin_service.hotel.Hotel;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adminId;
    @NotBlank(message = "Admin name is required")
    @Size(max = 100, message = "Admin name can't exceed 100 characters")
    private String adminName;

    @NotBlank(message = "Admin email is required")
    @Email(message = "Enter a valid email address")
    private String adminEmail;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Phone number must start with 6-9 and be exactly 10 digits"
    )
    private String adminPhoneNumber;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be 8 characters long")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#]).+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
    )
    private String adminPassword;
    private String role;
    private String status;
    private boolean active=true;
    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hotel> hotels = new ArrayList<>();

    private String resetToken;
}
