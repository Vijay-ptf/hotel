package com.hotel.admin_service.superAdmin;

import com.hotel.admin_service.admin.Admin;
import com.hotel.admin_service.admin.AdminDto;
import com.hotel.admin_service.admin.AdminRepository;
import com.hotel.admin_service.amenities.Amenities;
import com.hotel.admin_service.amenities.AmenitiesRepository;
import com.hotel.admin_service.hotel.Hotel;
import com.hotel.admin_service.hotel.HotelRepository;
import com.hotel.admin_service.hotel.dto.HotelAdminResponseDto;
import com.hotel.admin_service.security.exception.AccountExistException;
import com.hotel.admin_service.security.exception.AdminNotFoundException;
import com.hotel.admin_service.security.exception.NoHotelsFoundException;
import com.hotel.admin_service.security.exception.UnAuthorizedAccessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuperAdminService {

    private final SuperAdminRepository superAdminRepository;
    private final AdminRepository adminRepository;
    private final HotelRepository hotelRepository;
    private final AmenitiesRepository amenitiesRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public void adminReg(SuperAdmin superAdmin) {
        superAdmin.setRole("SUPER");
        superAdmin.setSuperAdminPassword(passwordEncoder.encode(superAdmin.getSuperAdminPassword()));
        Optional<SuperAdmin> adminOptional=superAdminRepository.findBySuperAdminEmail(superAdmin.getSuperAdminEmail());
        if (adminOptional.isPresent()){
            throw new AccountExistException("Account with provided email or password already exist");
        }
        superAdminRepository.save(superAdmin);
    }

    public Optional<SuperAdmin> findByEmail(String email) {
        return superAdminRepository.findBySuperAdminEmail(email);
    }

    public void approveAdminRequest(String email,Long adminId){
       Optional<SuperAdmin> adminOptional=superAdminRepository.findBySuperAdminEmail(email);
       if(adminOptional.isEmpty()){
           throw new AdminNotFoundException("super admin Not found with provided email"+" "+email);
       }
        Admin admin=adminRepository.findById(adminId)
                .orElseThrow(()->new AdminNotFoundException("Admin not found with provided id"+" "+adminId));
        admin.setStatus("APPROVED");
        adminRepository.save(admin);

    }

    public void rejectAdminRequest(String email,Long adminId){
        Optional<SuperAdmin> adminOptional=superAdminRepository.findBySuperAdminEmail(email);
        if(adminOptional.isEmpty()){
            throw new AdminNotFoundException("super admin Not found with provided email"+" "+email);
        }
        Admin admin=adminRepository.findById(adminId)
                .orElseThrow(()->new AdminNotFoundException("Admin not found with provided id"+" "+adminId));
        admin.setStatus("REJECTED");
        adminRepository.save(admin);

    }

    public List<AdminDto> pendingAdminList(String email){
        Optional<SuperAdmin> adminOptional=superAdminRepository.findBySuperAdminEmail(email);
        if(adminOptional.isEmpty()){
            throw new AdminNotFoundException("super admin Not found with provided email"+" "+email);
        }
        List<Admin> adminList=adminRepository.findByStatus("PENDING");
        return adminList.stream().map(
                dto->new AdminDto(
                        dto.getAdminId(),
                        dto.getAdminName(),
                        dto.getAdminEmail(),
                        dto.getAdminPhoneNumber()

                )).toList();
    }

    public List<AdminDto> approvedAdminList(String email){
        Optional<SuperAdmin> adminOptional=superAdminRepository.findBySuperAdminEmail(email);
        if(adminOptional.isEmpty()){
            throw new AdminNotFoundException("super admin Not found with provided email"+" "+email);
        }
        List<Admin> adminList=adminRepository.findByStatus("APPROVED");
        return adminList.stream().map(
                dto->new AdminDto(
                     dto.getAdminId(),
                     dto.getAdminName(),
                     dto.getAdminEmail(),
                     dto.getAdminPhoneNumber()
                )).toList();
    }

    public List<HotelAdminResponseDto> addedHotelByAdmin(Long adminId){
       Optional<Admin> adminOptional=adminRepository.findById(adminId);
       if(adminOptional.isEmpty()){
           throw new AdminNotFoundException("Admin not found with provided Id: "+" "+adminId);
       }
       List<Hotel> hotelList=hotelRepository.findHotelByAdmin_AdminId(adminId);
       return hotelList.stream().map(
               dto->new HotelAdminResponseDto(
                       dto.getAdmin().getAdminId(),
                       dto.getAdmin().getAdminName(),
                       dto.getHotelId(),
                       dto.getHotelName(),
                       dto.getDistrict().name(),
                       dto.getCreatedAt(),
                       dto.getUpdatedAt()
               )
       ).toList();
    }

    public void toggleAdminActivityStatus(Long adminId, boolean isActive){
        Optional<Admin> adminOptional=adminRepository.findById(adminId);
        if(adminOptional.isEmpty()){
            throw new AdminNotFoundException("Admin not found with provided Id: "+" "+adminId);
        }
        Admin admin=adminOptional.get();
        admin.setActive(isActive);
        adminRepository.save(admin);
    }

    @Transactional
    public String deleteHotelById(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new NoHotelsFoundException("Hotel not found with ID: " + hotelId));
        hotel.getAmenitiesList().clear();

        for (Amenities amenity : amenitiesRepository.findAll()) {
            amenity.getHotels().remove(hotel);
        }
        hotelRepository.delete(hotel);
        return "Hotel with ID " + hotelId + " deleted successfully";
    }
}
