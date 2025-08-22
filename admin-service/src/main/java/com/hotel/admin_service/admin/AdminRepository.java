package com.hotel.admin_service.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin,Long> {
    Optional<Admin> findByAdminEmail(String adminEmail);
    Optional<Admin> findByAdminEmailOrAdminPhoneNumber(String adminEmail,String adminPhoneNumber);
    List<Admin> findByStatus(String status);
    List<Admin> findByActive(boolean active);

    Optional<Admin> findByResetToken(String token);
}