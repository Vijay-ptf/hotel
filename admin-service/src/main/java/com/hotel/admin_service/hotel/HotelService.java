package com.hotel.admin_service.hotel;

import com.hotel.admin_service.admin.Admin;
import com.hotel.admin_service.admin.AdminRepository;
import com.hotel.admin_service.amenities.Amenities;
import com.hotel.admin_service.amenities.AmenitiesRepository;
import com.hotel.admin_service.hotel.dto.HotelRegisterRequest;
import com.hotel.admin_service.hotel.dto.HotelResponseDto;
import com.hotel.admin_service.hotel.dto.HotelUpdateRequest;
import com.hotel.admin_service.hotel.images.*;
import com.hotel.admin_service.hotel.landscape.Landscape;
import com.hotel.admin_service.hotel.landscape.LandscapeRepository;
import com.hotel.admin_service.hotel.types.HotelTypeRepository;
import com.hotel.admin_service.hotel.types.HotelTypes;
import com.hotel.admin_service.security.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private LandscapeRepository landscapeRepository;

    @Autowired
    private HotelTypeRepository hotelTypeRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AmenitiesRepository amenitiesRepository;

    @Autowired
    private HotelImageRepository hotelImageRepository;

    @Autowired
    private HotelImageUrlRepository hotelImageUrlRepository;

    public HotelResponseDto registerHotel(String email,HotelRegisterRequest request, MultipartFile[] imageFiles, List<String> imageUrls) throws Exception {
        Optional<Admin> adminOptional=adminRepository.findByAdminEmail(email);
        if(adminOptional.isEmpty()){
            throw new AdminNotFoundException("Admin not found");
        }
        Admin admin=adminOptional.get();


        Hotel hotel = new Hotel();
        hotel.setHotelName(request.getHotelName());
        hotel.setHotelDescription(request.getHotelDescription());
        hotel.setHotelRating(request.getHotelRating());
        hotel.setHotelBasicPricePerNight(request.getHotelBasicPricePerNight());
        hotel.setHotelAddress(request.getHotelAddress());
        hotel.setHotelEmail(request.getHotelEmail());
        hotel.setHotelPhoneNumber(request.getHotelPhoneNumber());
        hotel.setDistrict(District.valueOf(request.getDistrict()));
        hotel.setLocation(request.getLocation());
        hotel.setAdmin(admin);

        Landscape landscape = landscapeRepository.findById(request.getLandscapeId())
                .orElseThrow(() -> new RuntimeException("Landscape not found"));
        hotel.setLandscape(landscape);

        HotelTypes hotelTypes = hotelTypeRepository.findById(request.getHotelTypeId())
                .orElseThrow(() -> new RuntimeException("Hotel type not found"));
        hotel.setHotelTypes(hotelTypes);

        if (imageUrls != null) {
            for (String url : imageUrls) {
                HotelImageUrl imageUrl = new HotelImageUrl();
                imageUrl.setImageUrl(url);
                imageUrl.setHotel(hotel);
                hotel.getHotelImageUrls().add(imageUrl);
            }
        }

        if (imageFiles != null) {
            for (MultipartFile file : imageFiles) {
                if (!file.isEmpty()) {
                    HotelImage image = new HotelImage();
                    image.setImageData(file.getBytes());
                    image.setHotel(hotel);
                    hotel.getHotelImageUpload().add(image);
                }
            }
        }

        Hotel savedHotel = hotelRepository.save(hotel);
        return convertToDto(savedHotel);
    }

    private HotelResponseDto convertToDto(Hotel hotel) {
        HotelResponseDto dto = new HotelResponseDto();

        dto.setHotelId(hotel.getHotelId());
        dto.setHotelName(hotel.getHotelName());
        dto.setHotelDescription(hotel.getHotelDescription());
        dto.setHotelRating(hotel.getHotelRating());
        dto.setHotelBasicPricePerNight(hotel.getHotelBasicPricePerNight());
        dto.setHotelAddress(hotel.getHotelAddress());
        dto.setHotelEmail(hotel.getHotelEmail());
        dto.setHotelPhoneNumber(hotel.getHotelPhoneNumber());
        dto.setDistrict(hotel.getDistrict().name());
        dto.setLocation(hotel.getLocation());

        // Timestamps
        dto.setCreatedAt(hotel.getCreatedAt());
        dto.setUpdatedAt(hotel.getUpdatedAt());

        // Hotel Type
        if (hotel.getHotelTypes() != null) {
            dto.setHotelTypeId(hotel.getHotelTypes().getHotelTypeId());
            dto.setHotelTypeName(hotel.getHotelTypes().getHotelTypeName());
        }

        // Landscape Type
        if (hotel.getLandscape() != null) {
            dto.setLandscapeTypeId(hotel.getLandscape().getLandscapeId());
            dto.setLandscapeTypeName(hotel.getLandscape().getLandscapeTypeName());
        }

        // Hotel Images (URLs)
        if (hotel.getHotelImageUrls() != null && !hotel.getHotelImageUrls().isEmpty()) {
            dto.setHotelImageUrlsId(hotel.getHotelImageUrls().stream()
                    .map(HotelImageUrl::getId)
                    .toList());
            dto.setHotelImageUrls(hotel.getHotelImageUrls().stream()
                    .map(HotelImageUrl::getImageUrl)
                    .toList());
        }

        // Hotel Images (Base64)
        if (hotel.getHotelImageUpload() != null && !hotel.getHotelImageUpload().isEmpty()) {
            dto.setHotelImageUploadBase64Id(hotel.getHotelImageUpload().stream()
                    .map(HotelImage::getId)
                    .toList());
            dto.setHotelImageUploadBase64(hotel.getHotelImageUpload().stream()
                    .map(img -> Base64.getEncoder().encodeToString(img.getImageData()))
                    .toList());
        }

        // Amenities
        if (hotel.getAmenitiesList() != null && !hotel.getAmenitiesList().isEmpty()) {
            dto.setAmenitiesId(hotel.getAmenitiesList().stream()
                    .map(Amenities::getAmenitiesId)
                    .toList());
            dto.setAmenities(hotel.getAmenitiesList().stream()
                    .map(Amenities::getAmenitiesName)
                    .toList());
        }

        return dto;
    }




    @Transactional(readOnly = true)
    public List<HotelResponseDto> getAllHotels() {
        List<Hotel> hotels = hotelRepository.findAll();
        return hotels.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HotelResponseDto> getHotelsByDistrict(District district){
       List<Hotel> hotels=hotelRepository.findByDistrict(district);
        return hotels.stream().map(this::convertToDto).toList();
    }

    @Transactional(readOnly = true)
    public List<HotelResponseDto> getHotelsByLandscape(String landscape){
        List<Hotel> hotels=hotelRepository.findByLandscape_LandscapeTypeName(landscape);
        if(hotels.isEmpty()){
            throw new NoHotelsFoundException("No hotels found for landscape: " + landscape);
        }
        return hotels.stream().map(this::convertToDto).toList();
    }

    @Transactional
    public HotelResponseDto updateHotel(Long hotelId, HotelUpdateRequest request) {

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new NoHotelsFoundException("Hotel not found"));

        if (request.getHotelName() != null) {
            hotel.setHotelName(request.getHotelName());
        }
        if (request.getHotelDescription() != null) {
            hotel.setHotelDescription(request.getHotelDescription());
        }
        if (request.getHotelRating() != null) {
            hotel.setHotelRating(request.getHotelRating());
        }
        if (request.getHotelBasicPricePerNight() != null) {
            hotel.setHotelBasicPricePerNight(request.getHotelBasicPricePerNight());
        }
        if (request.getHotelAddress() != null) {
            hotel.setHotelAddress(request.getHotelAddress());
        }
        if (request.getHotelEmail() != null) {
            hotel.setHotelEmail(request.getHotelEmail());
        }
        if (request.getHotelPhoneNumber() != null) {
            hotel.setHotelPhoneNumber(request.getHotelPhoneNumber());
        }
        if (request.getDistrict() != null) {
            hotel.setDistrict(District.valueOf(request.getDistrict()));
        }
        if (request.getLocation() != null) {
            hotel.setLocation(request.getLocation());
        }

        if (request.getLandscapeId() != null) {
            Landscape landscape = landscapeRepository.findById(request.getLandscapeId())
                    .orElseThrow(() -> new LandscapeNotFoundException("Landscape not found"));
            hotel.setLandscape(landscape);
        }

        if (request.getHotelTypeId() != null) {
            HotelTypes hotelTypes = hotelTypeRepository.findById(request.getHotelTypeId())
                    .orElseThrow(() -> new HotelTypeNotFoundException("Hotel type not found"));
            hotel.setHotelTypes(hotelTypes);
        }

        Hotel updatedHotel = hotelRepository.save(hotel);
        return convertToDto(updatedHotel);
    }



    @Transactional
    public void deleteUploadedImage(Long hotelId, Long imageId) {
        HotelImage image = hotelImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));

        if (!image.getHotel().getHotelId().equals(hotelId)) {
            throw new IllegalArgumentException("Image does not belong to this hotel");
        }

        hotelImageRepository.delete(image);
    }

    @Transactional
    public void deleteImageUrl(Long hotelId, Long urlId) {
        HotelImageUrl url = hotelImageUrlRepository.findById(urlId)
                .orElseThrow(() -> new ResourceNotFoundException("Image URL not found"));

        if (!url.getHotel().getHotelId().equals(hotelId)) {
            throw new IllegalArgumentException("URL does not belong to this hotel");
        }

        hotelImageUrlRepository.delete(url);
    }

    public List<HotelImageResponse> getHotelImage(Long hotelId){
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new NoHotelsFoundException("Hotel not found"));
        List<HotelImage> hotelImages=hotelImageRepository.findByHotel_HotelId(hotelId);
        if(hotelImages.isEmpty()){
            throw new ResourceNotFoundException("No uploaded images found! Try to add one");
        }
        return hotelImages.stream().map(
                img-> new HotelImageResponse(
                        img.getId(),
                        Base64.getEncoder().encodeToString(img.getImageData())
                )
        ).toList();
    }

    public List<HotelImageUrlResponse> getHotelImageUrls(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new NoHotelsFoundException("Hotel not found"));
        List<HotelImageUrl> hotelImageUrls = hotelImageUrlRepository.findByHotel_HotelId(hotelId);
        return hotelImageUrls.stream().map(
                (img -> new HotelImageUrlResponse(
                        img.getId(),
                        img.getImageUrl()
                ))
        ).toList();
    }

    @Transactional
    public HotelResponseDto getHotelById(Long hotelId){
        Hotel hotel=hotelRepository.findById(hotelId).orElseThrow(()->new NoHotelsFoundException("Hotel Not Found"));
        return convertToDto(hotel);

    }

    public Landscape findLandscapeByHotel(Long hotelId){
        Landscape landscape=landscapeRepository.findByHotelId(hotelId);
        if(landscape == null){
            throw new LandscapeNotFoundException("Landscape not found");
        }
        return landscape;
    }

    public HotelTypes findHotelTypeByHotel(Long hotelId){
        HotelTypes hotelTypes=hotelTypeRepository.findByHotelId(hotelId);
        if(hotelTypes == null){
            throw new LandscapeNotFoundException("Landscape not found");
        }
        return hotelTypes;
    }

    public District findDistrict(Long hotelId){
        Hotel hotel=hotelRepository.findById(hotelId).orElseThrow(()->new NoHotelsFoundException("Hotel Not Found"));
        return hotel.getDistrict();
    }

    public String findPrice(Long hotelId){
        Hotel hotel=hotelRepository.findById(hotelId).orElseThrow(()->new NoHotelsFoundException("Hotel Not Found"));
        return hotel.getHotelBasicPricePerNight();
    }

    @Transactional
    public List<HotelResponseDto> getHotelByAdminEmail(String email){
        List<Hotel> hotels = hotelRepository.findHotelByAdmin_AdminEmail(email);
        return hotels.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }



}
