package com.hotel.admin_service.security;



import com.hotel.admin_service.admin.Admin;
import com.hotel.admin_service.admin.AdminRepository;
import com.hotel.admin_service.superAdmin.SuperAdmin;
import com.hotel.admin_service.superAdmin.SuperAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private SuperAdminRepository superAdminRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<SuperAdmin> superAdminOptional = superAdminRepository.findBySuperAdminEmail(email);
        if (superAdminOptional.isPresent()) {
            SuperAdmin superAdmin = superAdminOptional.get();
            return new org.springframework.security.core.userdetails.User(
                    superAdmin.getSuperAdminEmail(),
                    superAdmin.getSuperAdminPassword(),
                    List.of(new SimpleGrantedAuthority(superAdmin.getRole()))
            );
        }

        Optional<Admin> adminOpt = adminRepository.findByAdminEmail(email);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            return new org.springframework.security.core.userdetails.User(
                    admin.getAdminEmail(),
                    admin.getAdminPassword(),
                    List.of(new SimpleGrantedAuthority(admin.getRole()))
            );
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
