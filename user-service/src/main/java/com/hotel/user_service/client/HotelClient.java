package com.hotel.user_service.client;

import com.hotel.user_service.dto.HotelResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "admin-service", url = "http://localhost:8081", path = "/hotel")
public interface HotelClient {

    @GetMapping("/fetch-all-hotels")
    List<HotelResponseDto> retrieveAllHotels();

    @GetMapping("/fetch-hotels-by-district")
    List<HotelResponseDto> retrieveHotelsByDistrict(@RequestParam("district") String district);

    @GetMapping("/fetch-hotels-by-landscape")
    List<HotelResponseDto> retrieveHotelsByLandscape(@RequestParam String landscape);

}
