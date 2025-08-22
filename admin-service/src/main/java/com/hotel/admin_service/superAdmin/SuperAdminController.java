package com.hotel.admin_service.superAdmin;

import com.hotel.admin_service.admin.AdminDto;
import com.hotel.admin_service.hotel.dto.HotelAdminResponseDto;
import com.hotel.admin_service.hotel.images.CarouselImages;
import com.hotel.admin_service.hotel.images.CarouselImagesRepository;
import com.hotel.admin_service.security.JwtService;
import com.hotel.admin_service.security.exception.AdminNotFoundException;
import com.hotel.admin_service.security.exception.CarouselImageNotFoundException;
import com.hotel.admin_service.security.exception.CarouselImageUploadException;
import com.hotel.admin_service.security.exception.InvalidCredentialsException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/hotel/super-admin")
@Slf4j
public class SuperAdminController {

    @Autowired
    private SuperAdminService superAdminService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CarouselImagesRepository carouselImagesRepository;

    @PostMapping("/super-admin-register")
    public ResponseEntity<?> adminRegister(@RequestBody @Valid SuperAdmin admin) {
        log.info("Super admin registration request received: {}", admin.getSuperAdminEmail());
        superAdminService.adminReg(admin);
        log.info("Super admin registered successfully: {}", admin.getSuperAdminEmail());
        return ResponseEntity.ok("Admin Registration Successful");
    }

    @PostMapping("/super-admin-login")
    public ResponseEntity<?> superAdminLogin(@RequestBody SuperAdmin admin) {
        log.info("Login attempt for super admin with email: {}", admin.getSuperAdminEmail());
        Optional<SuperAdmin> loginAdmin = superAdminService.findByEmail(
                admin.getSuperAdminEmail());

        if (loginAdmin.isEmpty()) {
            log.error("Admin not found with provided email");
            throw new AdminNotFoundException("Admin not found with provided email");
        }

        SuperAdmin fetchedAdmin = loginAdmin.get();
        boolean passwordMatch = passwordEncoder.matches(
                admin.getSuperAdminPassword(), fetchedAdmin.getSuperAdminPassword());

        if (passwordMatch) {
            String token = jwtService.generateToken(fetchedAdmin.getSuperAdminEmail(), fetchedAdmin.getRole());
            log.info("Login successful for super admin: {}", fetchedAdmin.getSuperAdminEmail());
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "email", fetchedAdmin.getSuperAdminEmail(),
                    "role", fetchedAdmin.getRole()
            ));
        } else {
            log.warn("Invalid password attempt for super admin: {}", admin.getSuperAdminEmail());
            throw new InvalidCredentialsException("Invalid Credentials");
        }
    }

    @PostMapping("/approve-admin")
    public ResponseEntity<?> approveAdmin(Authentication authentication, @RequestParam Long adminId) {
        String email = authentication.getName();
        log.info("Super admin {} is approving admin request for ID: {}", email, adminId);
        superAdminService.approveAdminRequest(email, adminId);
        log.info("Admin approved successfully");
        return ResponseEntity.ok("Admin Request Approved " + email);
    }

    @PostMapping("/reject-admin")
    public ResponseEntity<?> rejectAdmin(Authentication authentication, @RequestParam Long adminId) {
        String email = authentication.getName();
        log.info("Super admin {} is rejecting admin request for ID: {}", email, adminId);
        superAdminService.rejectAdminRequest(email, adminId);
        log.info("Admin rejected successfully");
        return ResponseEntity.ok("Admin Request Rejected " + email);
    }

    @GetMapping("/pending-request")
    public ResponseEntity<?> pendingAdminRequestList(Authentication authentication) {
        String email = authentication.getName();
        log.info("Fetching pending admin requests by super admin: {}", email);
        List<AdminDto> adminList = superAdminService.pendingAdminList(email);
        log.info("Pending requests fetched: {}", adminList.size());
        return ResponseEntity.ok(adminList);
    }

    @GetMapping("/approved-admins")
    public ResponseEntity<?> approvedAdminRequestList(Authentication authentication) {
        String email = authentication.getName();
        log.info("Fetching approved admin requests by super admin: {}", email);
        List<AdminDto> adminList = superAdminService.approvedAdminList(email);
        log.info("Approved requests fetched: {}", adminList.size());
        return ResponseEntity.ok(adminList);
    }

    @GetMapping("/added-hotel-by-admin")
    public ResponseEntity<?> retrieveHotelsByAdmin(@RequestParam Long adminId) {
        log.info("Fetching hotels added by admin with ID: {}", adminId);
        List<HotelAdminResponseDto> hotelList = superAdminService.addedHotelByAdmin(adminId);
        log.info("Total hotels found: {}", hotelList.size());
        return ResponseEntity.ok(hotelList);
    }

    @PutMapping("/activity-status")
    public ResponseEntity<?> EditAdminActiveStatus(
            @RequestParam Long adminId,
            @RequestParam boolean isActive) {
        log.info("Request received to change activity status for Admin ID: {}", adminId);
        log.info("Requested status: {}", isActive ? "Activate" : "Deactivate");
        superAdminService.toggleAdminActivityStatus(adminId, isActive);
        log.info("Admin ID {} has been {}", adminId, isActive ? "activated" : "deactivated");
        return ResponseEntity.ok("Admin " + (isActive ? "activated" : "deactivated") + " successfully");
    }

    @DeleteMapping("/delete-hotel")
    public ResponseEntity<String> deleteHotel(@RequestParam Long hotelId) {
        log.info("DELETE request received for hotel ID: {}", hotelId);

        String result = superAdminService.deleteHotelById(hotelId);

        log.info("Hotel deletion completed: {}", result);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/add-carousel-image")
    public ResponseEntity<?> addCarouselImage(@RequestParam("file") MultipartFile file) throws IOException {

            if (file.isEmpty()) {
                throw new CarouselImageUploadException("Uploaded file is empty!");
            }
            CarouselImages carouselImages = new CarouselImages();
            carouselImages.setCarouselImage(file.getBytes());
            carouselImages.setState("Active");

            carouselImagesRepository.save(carouselImages);

            return ResponseEntity.ok("Carousel Image Saved Successfully");
    }


    @DeleteMapping("/delete-carousel-image")
    public ResponseEntity<?> deleteCarouselImage(@RequestParam Long carouselImageId) {
        CarouselImages carouselImages = carouselImagesRepository.findById(carouselImageId)
                .orElseThrow(() -> new CarouselImageNotFoundException("Selected Image not present"));

        carouselImagesRepository.deleteById(carouselImageId);
        return ResponseEntity.ok("Image deleted Successfully");
    }

    @PutMapping("/change-carousel-image-state")
    public ResponseEntity<?> changeCarouselImageState(@RequestParam Long carouselImageId,String newState) {
        CarouselImages carouselImages = carouselImagesRepository.findById(carouselImageId)
                .orElseThrow(() -> new CarouselImageNotFoundException("Selected Image not present"));
        carouselImages.setState(newState);
        carouselImagesRepository.save(carouselImages);
        return ResponseEntity.ok("Carousel State Updated");
    }

    @GetMapping("get-all-carousel-images")
    public ResponseEntity<?> retrieveAllCarouselImages(){
        List<CarouselImages> images=carouselImagesRepository.findAll();
        return ResponseEntity.ok(images);

    }

    @GetMapping("/get-active-images")
    public ResponseEntity<?> getActive(){
        List<CarouselImages> images=carouselImagesRepository.findByState("Active");
        return ResponseEntity.ok(images);
    }

}
