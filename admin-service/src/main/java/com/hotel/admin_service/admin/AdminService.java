package com.hotel.admin_service.admin;

import com.hotel.admin_service.security.exception.AccountExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public void adminReg(Admin admin) {
        admin.setRole("ADMIN");
        admin.setStatus("PENDING");
        admin.setAdminPassword(passwordEncoder.encode(admin.getAdminPassword()));
       Optional<Admin> adminOptional=adminRepository.findByAdminEmail(admin.getAdminEmail());
       if (adminOptional.isPresent()){
           throw new AccountExistException("Account with provided email or password already exist");
       }
        adminRepository.save(admin);
    }

    public Optional<Admin> findByEmail(String email) {
        return adminRepository.findByAdminEmail(email);
    }
}

