package com.hotel.admin_service.superAdmin;

import com.hotel.admin_service.admin.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuperAdminRepository extends JpaRepository<SuperAdmin,Long> {

    Optional<SuperAdmin> findBySuperAdminEmail(String superAdminEmail);
    Optional<SuperAdmin> findBySuperAdminEmailOrSuperAdminPhoneNumber(String superAdminEmail,String superAdminPhoneNumber);

    Optional<SuperAdmin> findByResetToken(String token);
}
