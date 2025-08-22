package com.hotel.admin_service.amenities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AmenitiesRequest {
    private Long hotelId;
    private List<Long> amenitiesIds;
}
