package com.hotel.admin_service.admin;

import com.hotel.admin_service.security.exception.AdminNotFoundException;
import com.hotel.admin_service.security.exception.InvalidCredentialsException;
import com.hotel.admin_service.security.JwtService;
import com.hotel.admin_service.security.exception.UnAuthorizedAccessException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/hotel/admin")
@Slf4j
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;


    @PostMapping("/admin-register")
    public ResponseEntity<?> adminRegister(@RequestBody @Valid Admin admin) {
        log.info("Admin registration attempt: email={}, phone={}",
                admin.getAdminEmail(), admin.getAdminPhoneNumber());
        adminService.adminReg(admin);
        log.info("Admin registered successfully: email={}", admin.getAdminEmail());
        return ResponseEntity.ok("Admin Registration Successful");
    }

    @PostMapping("/admin-login")
    public ResponseEntity<?> adminLogin(@RequestParam String email,
                                        @RequestParam String password) {
        log.info("Admin login attempt: email={}", email);

        // Only check by email now (phone number logic removed)
        Optional<Admin> loginAdmin = adminService.findByEmail(email);

        if (loginAdmin.isEmpty()) {
            log.warn("Admin not found: email={}", email);
            throw new AdminNotFoundException("Admin not found with provided email");
        }

        Admin fetchedAdmin = loginAdmin.get();

        // Password check (BCrypt)
        if (!passwordEncoder.matches(password, fetchedAdmin.getAdminPassword())) {
            throw new UnAuthorizedAccessException("Invalid password");
        }

        if (!fetchedAdmin.isActive()) {
            throw new UnAuthorizedAccessException("Account is deactivated by super admin");
        }

        return switch (fetchedAdmin.getStatus()) {
            case "APPROVED" -> {
                log.info("Admin login approved: email={}", fetchedAdmin.getAdminEmail());
                String token = jwtService.generateToken(
                        fetchedAdmin.getAdminEmail(),
                        fetchedAdmin.getRole()
                );
                yield ResponseEntity.ok(Map.of(
                        "token", token,
                        "email", fetchedAdmin.getAdminEmail(),
                        "role", fetchedAdmin.getRole()
                ));
            }
            case "REJECTED" -> {
                log.warn("Login attempt by rejected admin: email={}", fetchedAdmin.getAdminEmail());
                throw new UnAuthorizedAccessException("Your account was rejected. Access denied.");
            }
            default -> {
                log.info("Login attempt by unapproved admin: email={}", fetchedAdmin.getAdminEmail());
                throw new UnAuthorizedAccessException("Your account is not yet approved. Please wait for approval.");
            }
        };
    }




}
