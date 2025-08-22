package com.hotel.admin_service.OTP;

import com.hotel.admin_service.Email.EmailSender;
import com.hotel.admin_service.admin.Admin;
import com.hotel.admin_service.admin.AdminRepository;
import com.hotel.admin_service.superAdmin.SuperAdmin;
import com.hotel.admin_service.superAdmin.SuperAdminRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/hotel/otp")
@Slf4j
public class OTPController {

    @Autowired
    private OTPService otpService;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private SuperAdminRepository superAdminRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Simple email regex for basic validation
    private static final String SIMPLE_EMAIL_REGEX = "^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$";

    private String sanitizeSingleEmail(String raw) {
        if (raw == null) return null;
        String email = raw.trim().toLowerCase();
        if (email.contains(",")) {
            email = email.split(",")[0].trim();
        }
        return email;
    }

    private boolean isEmailInvalid(String email) {
        return (email == null || !email.matches(SIMPLE_EMAIL_REGEX));
    }

    /* ------------------- ADMIN OTP ------------------- */

    @PostMapping("admin/send")
    public ResponseEntity<?> sendOtp(@RequestParam String email) {
        String clean = sanitizeSingleEmail(email);
        if (isEmailInvalid(clean)) {
            return ResponseEntity.badRequest().body("Invalid email format.");
        }

        try {
            int otp = otpService.generateAndSaveOtp(clean);

            String htmlBody = "<div style='font-family: Arial, sans-serif; color: #333;'>"
                    + "<h2 style='color:#4CAF50;'>Hotel Management System - OTP Verification</h2>"
                    + "<p>Dear Admin,</p>"
                    + "<p>Your One-Time Password (OTP) is:</p>"
                    + "<h1 style='color:#4CAF50;'>" + otp + "</h1>"
                    + "<p>This OTP is valid for <b>5 minutes</b>.</p>"
                    + "<p>If you did not request this OTP, please ignore this message.</p>"
                    + "<br><p>Best Regards,<br><b>Hotel Management System Team</b></p>"
                    + "</div>";

            emailSender.sendRegEmail(clean, "OTP Verification - Hotel Management System", htmlBody);
            log.info("OTP sent successfully for email: {}", clean);
            return ResponseEntity.ok("OTP sent successfully to your email.");
        } catch (Exception e) {
            log.error("Failed to send OTP to email: {}", clean, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send OTP: " + e.getMessage());
        }
    }

    @PostMapping("admin/resend")
    public ResponseEntity<?> resendOtp(@RequestParam String email) {
        String clean = sanitizeSingleEmail(email);
        if (isEmailInvalid(clean)) {
            return ResponseEntity.badRequest().body("Invalid email format.");
        }

        try {
            if (otpService.resendOtp(clean)) {
                int otp = otpService.generateAndSaveOtp(clean);

                String htmlBody = "<div style='font-family: Arial, sans-serif; color: #333;'>"
                        + "<h2 style='color:#2196F3;'>Hotel Management System - OTP Resent</h2>"
                        + "<p>Dear User,</p>"
                        + "<p>Your new OTP is:</p>"
                        + "<h1 style='color:#2196F3;'>" + otp + "</h1>"
                        + "<p>This OTP is valid for <b>5 minutes</b>.</p>"
                        + "<p>If you did not request this OTP, please ignore this message.</p>"
                        + "<br><p>Best Regards,<br><b>Hotel Management System Team</b></p>"
                        + "</div>";

                emailSender.sendRegEmail(clean, "OTP Resend - Hotel Management System", htmlBody);
                log.info("OTP resent successfully for email: {}", clean);
                return ResponseEntity.ok("OTP resent successfully to your email.");
            }
            return ResponseEntity.badRequest().body("No OTP request found for this email.");
        } catch (Exception e) {
            log.error("Failed to resend OTP to email: {}", clean, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to resend OTP: " + e.getMessage());
        }
    }

    @PostMapping("admin/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OTP otpRequest) {
        String clean = sanitizeSingleEmail(otpRequest.getEmail());
        if (isEmailInvalid(clean)) {
            return ResponseEntity.badRequest().body("Invalid email format.");
        }

        boolean isValid = otpService.verifyOtp(clean, otpRequest.getOtp());
        if (isValid) {
            log.info("OTP verified successfully for email: {}", clean);
            return ResponseEntity.ok("OTP Verified Successfully!");
        }
        log.warn("Invalid or expired OTP for email: {}", clean);
        return ResponseEntity.badRequest().body("Invalid or expired OTP.");
    }

    /* ------------------- ADMIN PASSWORD RESET ------------------- */

    @PostMapping("/admin-forgot-password")
    public ResponseEntity<?> adminForgotPassword(@RequestParam String email) {
        String clean = sanitizeSingleEmail(email);
        if (isEmailInvalid(clean)) {
            return ResponseEntity.badRequest().body("Invalid email format.");
        }

        Optional<Admin> adminOptional = adminRepository.findByAdminEmail(clean);
        if (adminOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found with the provided email.");
        }

        Admin admin = adminOptional.get();
        String token = UUID.randomUUID().toString();
        admin.setResetToken(token);
        adminRepository.save(admin);

        String resetURL = "http://localhost:5173/admin/reset-password?token=" + token;

        String htmlBody = "<div style='font-family: Arial, sans-serif; color: #333;'>"
                + "<h2 style='color:#E53935;'>Hotel Management System - Password Reset</h2>"
                + "<p>Dear " + admin.getAdminName() + ",</p>"
                + "<p>We received a request to reset your admin account password.</p>"
                + "<p>Click the button below to reset your password:</p>"
                + "<a href='" + resetURL + "' style='display:inline-block;padding:10px 20px;"
                + "color:white;background-color:#E53935;text-decoration:none;border-radius:5px;'>Reset Password</a>"
                + "<p>This link is valid for <b>1 hour</b>. If you did not request this reset, please ignore this email.</p>"
                + "<br><p>Best Regards,<br><b>Hotel Management System Team</b></p>"
                + "</div>";

        try {
            emailSender.sendRegEmail(admin.getAdminEmail(), "Admin Password Reset - Hotel Management System", htmlBody);
            log.info("Password reset link sent to admin email: {}", clean);
            return ResponseEntity.ok("Password reset link sent to your registered email.");
        } catch (Exception e) {
            log.error("Failed to send password reset link to admin: {}", clean, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send reset link: " + e.getMessage());
        }
    }

    @PostMapping("/admin-reset-password")
    public ResponseEntity<?> adminResetPassword(@RequestParam String token,
                                                @RequestParam String newPassword) {
        Optional<Admin> adminOptional = adminRepository.findByResetToken(token);

        if (adminOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid reset token.");
        }

        Admin admin = adminOptional.get();
        admin.setAdminPassword(passwordEncoder.encode(newPassword));
        admin.setResetToken(null);
        adminRepository.save(admin);

        return ResponseEntity.ok("Password reset successfully.");
    }

    /* ------------------- SUPER ADMIN OTP ------------------- */

    @PostMapping("/super-admin/send")
    public ResponseEntity<?> sendSuperAdminOtp(@RequestParam String email) {
        String clean = sanitizeSingleEmail(email);
        if (isEmailInvalid(clean)) {
            return ResponseEntity.badRequest().body("Invalid email format.");
        }

        try {
            int otp = otpService.generateAndSaveOtp(clean);

            String htmlBody = "<div style='font-family: Arial, sans-serif; color: #333;'>"
                    + "<h2 style='color:#FF9800;'>Hotel Management System - Super Admin OTP</h2>"
                    + "<p>Dear Super Admin,</p>"
                    + "<p>Your OTP for accessing high-level system features is:</p>"
                    + "<h1 style='color:#FF9800;'>" + otp + "</h1>"
                    + "<p>This OTP is valid for <b>5 minutes</b>. If you did not request this OTP, please contact support immediately.</p>"
                    + "<br><p>Best Regards,<br><b>Hotel Management System Security Team</b></p>"
                    + "</div>";

            emailSender.sendRegEmail(clean, "Super Admin OTP Verification - Hotel Management System", htmlBody);
            log.info("Super Admin OTP sent to: {}", clean);
            return ResponseEntity.ok("OTP sent successfully to super admin email.");
        } catch (Exception e) {
            log.error("Failed to send Super Admin OTP to {}: {}", clean, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send OTP: " + e.getMessage());
        }
    }

    @PostMapping("/super-admin/resend")
    public ResponseEntity<?> resendSuperAdminOtp(@RequestParam String email) {
        String clean = sanitizeSingleEmail(email);
        if (isEmailInvalid(clean)) {
            return ResponseEntity.badRequest().body("Invalid email format.");
        }

        try {
            if (otpService.resendOtp(clean)) {
                int otp = otpService.generateAndSaveOtp(clean);

                String htmlBody = "<div style='font-family: Arial, sans-serif; color: #333;'>"
                        + "<h2 style='color:#FF9800;'>Hotel Management System - Super Admin OTP Resent</h2>"
                        + "<p>Dear Super Admin,</p>"
                        + "<p>Your new OTP for accessing high-level system features is:</p>"
                        + "<h1 style='color:#FF9800;'>" + otp + "</h1>"
                        + "<p>This OTP is valid for <b>5 minutes</b>. If you did not request this OTP, please contact support immediately.</p>"
                        + "<br><p>Best Regards,<br><b>Hotel Management System Security Team</b></p>"
                        + "</div>";

                emailSender.sendRegEmail(clean, "Super Admin OTP Resend - Hotel Management System", htmlBody);
                log.info("Super Admin OTP resent successfully for email: {}", clean);
                return ResponseEntity.ok("OTP resent successfully to your email.");
            }
            return ResponseEntity.badRequest().body("No OTP request found for this email.");
        } catch (Exception e) {
            log.error("Failed to resend Super Admin OTP to email: {}", clean, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to resend OTP: " + e.getMessage());
        }
    }

    @PostMapping("/super-admin/verify")
    public ResponseEntity<?> verifySuperAdminOtp(@RequestBody OTP otpRequest) {
        String clean = sanitizeSingleEmail(otpRequest.getEmail());
        if (isEmailInvalid(clean)) {
            return ResponseEntity.badRequest().body("Invalid email format.");
        }

        boolean isValid = otpService.verifyOtp(clean, otpRequest.getOtp());
        if (isValid) {
            log.info("Super Admin OTP verified successfully for email: {}", clean);
            return ResponseEntity.ok("OTP Verified Successfully!");
        }
        log.warn("Invalid or expired Super Admin OTP for email: {}", clean);
        return ResponseEntity.badRequest().body("Invalid or expired OTP.");
    }

    /* ------------------- SUPER ADMIN PASSWORD RESET ------------------- */

    @PostMapping("/super-admin-forgot-password")
    public ResponseEntity<?> superAdminForgotPassword(@RequestParam String email) {
        String clean = sanitizeSingleEmail(email);
        if (isEmailInvalid(clean)) {
            return ResponseEntity.badRequest().body("Invalid email format.");
        }

        Optional<SuperAdmin> superAdminOptional = superAdminRepository.findBySuperAdminEmail(clean);
        if (superAdminOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Super Admin not found with the provided email.");
        }

        SuperAdmin superAdmin = superAdminOptional.get();
        String token = UUID.randomUUID().toString();
        superAdmin.setResetToken(token);
        superAdminRepository.save(superAdmin);

        String resetURL = "http://localhost:5173/super-admin/reset-password?token=" + token;

        String htmlBody = "<div style='font-family: Arial, sans-serif; color: #333;'>"
                + "<h2 style='color:#E53935;'>Hotel Management System - Super Admin Password Reset</h2>"
                + "<p>Dear " + superAdmin.getSuperAdminName() + ",</p>"
                + "<p>We received a request to reset your Super Admin account password.</p>"
                + "<p>Click the button below to reset your password:</p>"
                + "<a href='" + resetURL + "' style='display:inline-block;padding:10px 20px;"
                + "color:white;background-color:#E53935;text-decoration:none;border-radius:5px;'>Reset Password</a>"
                + "<p>This link is valid for <b>1 hour</b>. If you did not request this reset, please ignore this email.</p>"
                + "<br><p>Best Regards,<br><b>Hotel Management System Security Team</b></p>"
                + "</div>";

        try {
            emailSender.sendRegEmail(superAdmin.getSuperAdminEmail(), "Super Admin Password Reset - Hotel Management System", htmlBody);
            log.info("Super Admin password reset link sent to: {}", clean);
            return ResponseEntity.ok("Password reset link sent to your registered email.");
        } catch (Exception e) {
            log.error("Failed to send Super Admin password reset link to {}: {}", clean, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send reset link: " + e.getMessage());
        }
    }

    @PostMapping("/super-admin-reset-password")
    public ResponseEntity<?> superAdminResetPassword(@RequestParam String token,
                                                     @RequestParam String newPassword) {
        Optional<SuperAdmin> superAdminOptional = superAdminRepository.findByResetToken(token);

        if (superAdminOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid reset token.");
        }

        SuperAdmin superAdmin = superAdminOptional.get();
        superAdmin.setSuperAdminPassword(passwordEncoder.encode(newPassword));
        superAdmin.setResetToken(null);
        superAdminRepository.save(superAdmin);

        return ResponseEntity.ok("Password reset successfully.");
    }
}
