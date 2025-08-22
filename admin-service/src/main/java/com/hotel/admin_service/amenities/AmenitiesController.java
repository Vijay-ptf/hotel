package com.hotel.admin_service.amenities;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotel/amenities")
@RequiredArgsConstructor
@Slf4j
public class AmenitiesController {

    private final AmenitiesService amenitiesService;
    private final AmenitiesRepository amenitiesRepository;

    @PostMapping("/add")
    public ResponseEntity<Amenities> addAmenities(@RequestBody Amenities amenities) {
        log.info("Request to add amenities: {}", amenities.getAmenitiesName());
        Amenities added = amenitiesService.addAmenitiesToHotel(amenities);
        log.info("Amenities added successfully with ID: {}", added.getAmenitiesId());
        return ResponseEntity.ok(added);
    }

    @GetMapping("/retrieve-all-amenities")
    public ResponseEntity<?> getAllAmenities() {
        log.info("Fetching all amenities...");
        List<Amenities> amenitiesList = amenitiesService.getAllAmenities();
        log.info("Total amenities retrieved: {}", amenitiesList.size());
        return ResponseEntity.ok(amenitiesList);
    }

    @PostMapping("/assign-to-hotel")
    public ResponseEntity<String> assignAmenitiesToHotel(@RequestBody AmenitiesRequest request) {
        log.info("Assigning amenities to hotel - Hotel ID: {}, Amenities IDs: {}",
                request.getHotelId(), request.getAmenitiesIds());
        String message = amenitiesService.assignAmenitiesToHotel(request);
        log.info("Amenities assigned successfully to hotel ID: {}", request.getHotelId());
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/remove-amenities-from-hotel")
    public ResponseEntity<?> deleteAmenities(@RequestParam Long hotelId, @RequestParam Long amenitiesId) {
        log.info("Removing amenities ID: {} from hotel ID: {}", amenitiesId, hotelId);
        amenitiesService.deleteAmenities(hotelId, amenitiesId);
        log.info("Amenities removed successfully");
        return ResponseEntity.ok("Amenities removed from hotel");
    }

    @DeleteMapping("/delete-amenities")
    public ResponseEntity<?> deleteAddedAmenities(@RequestParam Long amenitiesId){
        amenitiesService.deleteAddedAmenities(amenitiesId);
        return ResponseEntity.ok("Amenity Deleted");
    }

    @PutMapping("/edit-amenities")
    public ResponseEntity<?> updateAmenity(@RequestParam Long amenitiesId,@RequestParam String newName){
        amenitiesService.editAmenitiesName(amenitiesId,newName);
        return ResponseEntity.ok("Amenity name edited: "+ newName);
    }

    @GetMapping("/get-amenities-by-hotel")
    public ResponseEntity<?> retrieveAmenitiesByHotelId(@RequestParam Long hotelId){
       List<AmenitiesRequest> amenities=amenitiesService.getAmenitiesByHotelId(hotelId);
       return ResponseEntity.ok(amenities);
    }

    @GetMapping("/total-amenities-count")
    public ResponseEntity<?> getTotalAmenitiesCount(){
        List<Amenities> amenitiesList=amenitiesRepository.findAll();
        return ResponseEntity.ok(amenitiesList.size());
    }

}