package com.hotel.admin_service.hotel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.admin_service.amenities.Amenities;
import com.hotel.admin_service.hotel.dto.HotelRegisterRequest;
import com.hotel.admin_service.hotel.dto.HotelResponseDto;
import com.hotel.admin_service.hotel.dto.HotelUpdateRequest;
import com.hotel.admin_service.hotel.images.*;
import com.hotel.admin_service.hotel.landscape.Landscape;
import com.hotel.admin_service.hotel.types.HotelTypes;
import com.hotel.admin_service.security.exception.NoHotelsFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/hotel")
@Slf4j
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @Autowired
    private HotelImageRepository hotelImageRepository;
    @Autowired
    private HotelImageUrlRepository hotelImageUrlRepository;
    @Autowired
    private HotelRepository hotelRepository;


    @PostMapping(value = "/register-hotel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HotelResponseDto> registerHotel(
            @RequestPart("hotelData") HotelRegisterRequest request,
            @RequestPart(value = "imageFiles", required = false) MultipartFile[] imageFiles,
            @RequestPart(value = "imageUrls", required = false) String imageUrlsJson,
            Principal principal
    ) throws Exception {
        List<String> imageUrls = new ArrayList<>();
        if (imageUrlsJson != null && !imageUrlsJson.isEmpty()) {
            imageUrls = new ObjectMapper().readValue(imageUrlsJson, new TypeReference<List<String>>() {});
        }
        String adminEmail = principal.getName();
        HotelResponseDto response = hotelService.registerHotel(adminEmail, request, imageFiles, imageUrls);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/districts")
    public ResponseEntity<List<Map<String, String>>> getDistrictsWithNames() {
        log.info("Fetching all districts");
        List<Map<String, String>> districts = Arrays.stream(District.values())
                .map(d -> Map.of("key", d.name(), "name", d.getDisplayName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(districts);
    }

    @GetMapping("/fetch-all-hotels")
    public ResponseEntity<?> retrieveAllHotels() {
        log.info("Fetching all hotels");
        List<HotelResponseDto> hotels = hotelService.getAllHotels();
        log.info("Fetched {} hotels", hotels.size());
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/fetch-hotels-by-district")
    public ResponseEntity<?> retrieveHotelsByDistrict(@RequestParam("district") String districtStr) {
        try {
            log.info("Fetching hotels by district: {}", districtStr);
            District district = District.valueOf(districtStr.toUpperCase());
            List<HotelResponseDto> hotels = hotelService.getHotelsByDistrict(district);
            log.info("Fetched {} hotels in district {}", hotels.size(), districtStr);
            return ResponseEntity.ok(hotels);
        } catch (Exception e) {
            log.error("Invalid district: {}", districtStr, e);
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

    @GetMapping("/fetch-hotels-by-landscape")
    public ResponseEntity<?> retrieveHotelsByLandscape(@RequestParam String landscape) {
        log.info("Fetching hotels by landscape: {}", landscape);
        List<HotelResponseDto> hotels = hotelService.getHotelsByLandscape(landscape);
        log.info("Fetched {} hotels with landscape {}", hotels.size(), landscape);
        return ResponseEntity.ok(hotels);
    }

    @PutMapping(value = "/edit-hotel-details", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HotelResponseDto> updateHotel(
            @RequestParam Long hotelId,
            @RequestPart("hotelData") HotelUpdateRequest request) {

        log.info("Updating hotel with ID: {}", hotelId);
        HotelResponseDto updatedHotel = hotelService.updateHotel(hotelId, request);
        log.info("Hotel updated successfully with ID: {}", updatedHotel.getHotelId());
        return ResponseEntity.ok(updatedHotel);
    }

    @DeleteMapping("/delete-uploaded-images")
    public ResponseEntity<String> deleteHotelImage(
            @RequestParam Long hotelId,
            @RequestParam Long imageId) {
        log.info("Deleting uploaded image with ID: {} from hotel ID: {}", imageId, hotelId);
        hotelService.deleteUploadedImage(hotelId, imageId);
        return ResponseEntity.ok("Image deleted successfully");
    }

    @DeleteMapping("/delete-image-urls")
    public ResponseEntity<String> deleteHotelImageUrl(
            @RequestParam Long hotelId,
            @RequestParam Long urlId) {
        log.info("Deleting image URL with ID: {} from hotel ID: {}", urlId, hotelId);
        hotelService.deleteImageUrl(hotelId, urlId);
        return ResponseEntity.ok("Image URL deleted successfully");
    }

    @PostMapping("/add-hotel-image")
    public ResponseEntity<?> addHotelImage(
            @RequestParam Long hotelId,
            @RequestPart("imageFile") MultipartFile imageFile) throws IOException {

        log.info("Adding image for hotel ID: {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new NoHotelsFoundException("Hotel not found"));

        HotelImage image = new HotelImage();
        image.setImageData(imageFile.getBytes());
        image.setHotel(hotel);
        hotelImageRepository.save(image);

        log.info("Image added successfully to hotel ID: {}", hotelId);
        return ResponseEntity.ok("Image added successfully");
    }

    @PostMapping("/add-hotel-image-url")
    public ResponseEntity<?> addHotelImageUrl(
            @RequestParam Long hotelId,
            @RequestParam String imageUrl) {

        log.info("Adding image URL for hotel ID: {} - URL: {}", hotelId, imageUrl);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new NoHotelsFoundException("Hotel not found"));

        HotelImageUrl url = new HotelImageUrl();
        url.setImageUrl(imageUrl);
        url.setHotel(hotel);
        hotelImageUrlRepository.save(url);

        log.info("Image URL added successfully to hotel ID: {}", hotelId);
        return ResponseEntity.ok("Image URL added successfully");
    }

    @GetMapping("/get-hotel-images")
    public ResponseEntity<?> getHotelImages(@RequestParam Long hotelId) {
        log.info("Fetching uploaded images for hotel ID: {}", hotelId);
        List<HotelImageResponse> hotelImages = hotelService.getHotelImage(hotelId);
        log.info("Fetched {} uploaded images for hotel ID: {}", hotelImages.size(), hotelId);
        return ResponseEntity.ok(hotelImages);
    }

    @GetMapping("/get-hotel-image-urls")
    public ResponseEntity<?> getHotelImageUrl(@RequestParam Long hotelId) {
        log.info("Fetching image URLs for hotel ID: {}", hotelId);
        List<HotelImageUrlResponse> hotelImageUrls = hotelService.getHotelImageUrls(hotelId);
        log.info("Fetched {} image URLs for hotel ID: {}", hotelImageUrls.size(), hotelId);
        return ResponseEntity.ok(hotelImageUrls);
    }

    @GetMapping("/get-by-hotel-id")
    public ResponseEntity<HotelResponseDto> getHotelById(@RequestParam Long hotelId) {
        HotelResponseDto hotel = hotelService.getHotelById(hotelId);
        return ResponseEntity.ok(hotel);
    }

    @GetMapping("/get-hotel-landscape")
    public ResponseEntity<?> getLandscapeByHotel(@RequestParam Long hotelId){
       Landscape landscape =hotelService.findLandscapeByHotel(hotelId);
       return ResponseEntity.ok(landscape);
    }

    @GetMapping("/get-hotel-type")
    public ResponseEntity<?> getHotelTypeByHotel(@RequestParam Long hotelId){
       HotelTypes hotelTypes =hotelService.findHotelTypeByHotel(hotelId);
       return ResponseEntity.ok(hotelTypes);
    }

    @GetMapping("/get-hotel-district")
    public ResponseEntity<?> getDistrictByHotel(@RequestParam Long hotelId){
       District district =hotelService.findDistrict(hotelId);
       return ResponseEntity.ok(district);
    }

    @GetMapping("/get-hotel-price")
    public ResponseEntity<?> getPriceByHotel(@RequestParam Long hotelId){
       String price =hotelService.findPrice(hotelId);
       return ResponseEntity.ok(price);
    }

    @GetMapping("/total-hotel-count")
    public ResponseEntity<?> getTotalHotelsCount(){
        List<Hotel> hotels=hotelRepository.findAll();
        return ResponseEntity.ok(hotels.size());
    }

    @GetMapping("/total-hotels-added-by-admin-count")
    public ResponseEntity<?> getTotalHotelsAddedByAdminCount(Authentication authentication){
        String email=authentication.getName();
        List<Hotel> hotels=hotelRepository.findHotelByAdmin_AdminEmail(email);
        return ResponseEntity.ok(hotels.size());
    }


    @GetMapping("/added-hotel-by-admin-email")
    public ResponseEntity<?> getHotelAddedByAdminEmail(Authentication authentication){
        String email=authentication.getName();
        List<HotelResponseDto> hotels=hotelService.getHotelByAdminEmail(email);
        return ResponseEntity.ok(hotels);
    }



}
