package com.hotel.user_service.controller;

import com.hotel.user_service.client.HotelClient;
import com.hotel.user_service.dto.HotelResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final HotelClient hotelClient;

    @GetMapping("/fetch-all-hotels")
    public ResponseEntity<?> getAllHotels(){
        List<HotelResponseDto> hotels=hotelClient.retrieveAllHotels();
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/fetch-hotels-by-district")
    public ResponseEntity<?> getHotelsByDistrict(@RequestParam("district") String district){
        return ResponseEntity.ok(hotelClient.retrieveHotelsByDistrict(district));
    }

    @GetMapping("/fetch-hotels-by-landscape")
    public ResponseEntity<?> getHotelsByLandscape(@RequestParam String landscape){
        return ResponseEntity.ok(hotelClient.retrieveHotelsByLandscape(landscape));
    }

}
