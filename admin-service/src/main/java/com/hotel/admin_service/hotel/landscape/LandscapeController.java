package com.hotel.admin_service.hotel.landscape;

import com.hotel.admin_service.security.exception.LandscapeEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotel/landscape")
@Slf4j
public class LandscapeController {

    @Autowired
    private LandscapeService landscapeService;

    @PostMapping("/create")
    public ResponseEntity<?> createLandscapeType(@RequestBody Landscape landscape) {
        log.info("Request to create landscape type: {}", landscape.getLandscapeTypeName());
        landscapeService.addLandscapeType(landscape);
        log.info("Landscape type added successfully: {}", landscape.getLandscapeTypeName());
        return ResponseEntity.ok("Landscape Added");
    }

    @GetMapping("/get-all-landscapes")
    public ResponseEntity<?> getAllLandscapes() {
        log.info("Fetching all landscape types...");
        List<Landscape> landscapes = landscapeService.getAllLandscapes();
        if (landscapes.isEmpty()) {
            log.warn("Landscape list is empty");
            throw new LandscapeEmptyException("Landscapes are empty");
        }
        log.info("Total landscapes retrieved: {}", landscapes.size());
        return ResponseEntity.ok(landscapes);
    }
}
