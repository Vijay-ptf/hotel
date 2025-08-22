package com.hotel.admin_service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/hotel/super-admin/super-admin-register").permitAll()
                        .requestMatchers("/hotel/super-admin/super-admin-login").permitAll()
                        .requestMatchers("/hotel/admin/admin-register").permitAll()
                        .requestMatchers("/hotel/admin/admin-login").permitAll()
                        .requestMatchers("/hotel/districts").permitAll()
                        .requestMatchers("/hotel/get-all-hotel-types").permitAll()
                        .requestMatchers("/hotel/landscape/get-all-landscapes").permitAll()
                        .requestMatchers("/hotel/fetch-all-hotels").permitAll()
                        .requestMatchers("/hotel/fetch-hotels-by-district").permitAll()
                        .requestMatchers("/hotel/fetch-hotels-by-landscape").permitAll()
                        .requestMatchers("/hotel/get-hotel-landscape").permitAll()
                        .requestMatchers("/hotel/get-hotel-type").permitAll()
                        .requestMatchers("/hotel/get-hotel-district").permitAll()
                        .requestMatchers("/hotel/get-hotel-price").permitAll()
                        .requestMatchers("/hotel/amenities/retrieve-all-amenities").permitAll()
                        .requestMatchers("/hotel/super-admin/pending-request").permitAll()
                        .requestMatchers("/hotel/super-admin/approved-admins").permitAll()
                        .requestMatchers("/hotel/get-hotel-image-urls").permitAll()
                        .requestMatchers("/hotel/get-hotel-images").permitAll()
                        .requestMatchers("/hotel/amenities/get-amenities-by-hotel").permitAll()
                        .requestMatchers("/hotel/amenities/total-amenities-count").permitAll()
                        .requestMatchers("/hotel/get-by-hotel-id").permitAll()
                        .requestMatchers("/hotel/total-hotel-count").permitAll()
                        .requestMatchers("/hotel/otp/admin/send").permitAll()
                        .requestMatchers("/hotel/otp/admin/resend").permitAll()
                        .requestMatchers("/hotel/otp/admin/verify").permitAll()
                        .requestMatchers("/hotel/otp/admin-forgot-password").permitAll()
                        .requestMatchers("/hotel/otp/admin-reset-password").permitAll()
                        .requestMatchers("/hotel/otp/super-admin/send").permitAll()
                        .requestMatchers("/hotel/otp/super-admin/verify").permitAll()
                        .requestMatchers("/hotel/otp/super-admin/super-admin-forgot-password").permitAll()
                        .requestMatchers("/hotel/otp/super-admin/super-admin-reset-password").permitAll()
                        .requestMatchers("/hotel/super-admin/get-all-carousel-images").permitAll()
                        .requestMatchers("/hotel/super-admin/get-active-images").permitAll()
                        .requestMatchers("/hotel/landscape/create").hasAuthority("ADMIN")
                        .requestMatchers("/hotel/add-hotel-type").hasAuthority("ADMIN")
                        .requestMatchers("/hotel/added-hotel-by-admin-email").hasAuthority("ADMIN")
                        .requestMatchers("/hotel/amenities/add").hasAnyAuthority("ADMIN","SUPER")
                        .requestMatchers("/hotel/export").hasAnyAuthority("ADMIN","SUPER")
                        .requestMatchers("/hotel/total-hotels-added-by-admin-count").hasAnyAuthority("ADMIN","SUPER")
                        .requestMatchers("/hotel/amenities/assign-to-hotel").hasAnyAuthority("ADMIN","SUPER")
                        .requestMatchers("/hotel/amenities/remove-amenities-from-hotel").hasAnyAuthority("ADMIN","SUPER")
                        .requestMatchers("/hotel/amenities/delete-amenities").hasAnyAuthority("ADMIN","SUPER")
                        .requestMatchers("/hotel/amenities/edit-amenities").hasAnyAuthority("ADMIN","SUPER")
                        .requestMatchers("/hotel/edit-hotel-details").hasAnyAuthority("ADMIN","SUPER")
                        .requestMatchers("/hotel/add-hotel-image").hasAnyAuthority("ADMIN","SUPER")
                        .requestMatchers("/hotel/add-hotel-image-url").hasAnyAuthority("ADMIN","SUPER")
                        .requestMatchers("/hotel/delete-uploaded-images").hasAnyAuthority("ADMIN","SUPER")
                        .requestMatchers("/hotel/delete-image-urls").hasAnyAuthority("ADMIN","SUPER")
                        .requestMatchers("/hotel/super-admin/added-hotel-by-admin").hasAnyAuthority("ADMIN","SUPER")
                        .requestMatchers("/hotel/super-admin/activity-status").hasAnyAuthority("ADMIN","SUPER")
                        .requestMatchers("/hotel/super-admin/delete-hotel").hasAnyAuthority("ADMIN","SUPER")
                        .requestMatchers("/hotel/register-hotel").hasAuthority("ADMIN")
                        .requestMatchers("/hotel/super-admin/approve-admin").hasAuthority("SUPER")
                        .requestMatchers("/hotel/super-admin/reject-admin").hasAuthority("SUPER")
                        .requestMatchers("/hotel/super-admin/add-carousel-image").hasAuthority("SUPER")
                        .requestMatchers("/hotel/super-admin/delete-carousel-image").hasAuthority("SUPER")
                        .requestMatchers("/hotel/super-admin/change-carousel-image-state").hasAuthority("SUPER")
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}

