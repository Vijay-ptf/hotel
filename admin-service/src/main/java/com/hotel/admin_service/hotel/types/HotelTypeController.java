package com.hotel.admin_service.hotel.types;

import com.hotel.admin_service.security.exception.HotelTypeEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotel")
@Slf4j
public class HotelTypeController {

    @Autowired
    private HotelTypeService hotelTypeService;

    @PostMapping("/add-hotel-type")
    public ResponseEntity<?> createHotelType(@RequestBody HotelTypes hotelTypes) {
        log.info("Request to add new hotel type: {}", hotelTypes.getHotelTypeName());
        hotelTypeService.addHotelType(hotelTypes);
        log.info("Hotel type added successfully: {}", hotelTypes.getHotelTypeName());
        return ResponseEntity.ok("Hotel Type added successfully");
    }

    @GetMapping("/get-all-hotel-types")
    public ResponseEntity<?> getAllTypes() {
        log.info("Fetching all hotel types...");
        List<HotelTypes> hotelTypes = hotelTypeService.getAllHotelTypes();
        if (hotelTypes.isEmpty()) {
            log.warn("Hotel types list is empty");
            throw new HotelTypeEmptyException("Hotel Types are empty!");
        }
        log.info("Total hotel types retrieved: {}", hotelTypes.size());
        return ResponseEntity.ok(hotelTypes);
    }
}
