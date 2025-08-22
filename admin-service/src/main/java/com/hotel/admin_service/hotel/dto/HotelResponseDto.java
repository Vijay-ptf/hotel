package com.hotel.admin_service.hotel.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class HotelResponseDto {
    private Long hotelId;
    private String hotelName;
    private String hotelDescription;
    private Double hotelRating;
    private String hotelBasicPricePerNight;
    private String hotelAddress;
    private String hotelEmail;
    private String hotelPhoneNumber;
    private String district;
    private String location;

    // Hotel Type
    private Long hotelTypeId;
    private String hotelTypeName;

    // Landscape Type
    private Long landscapeTypeId;
    private String landscapeTypeName;

    // Hotel Images (URL + ID)
    private List<Long> hotelImageUrlsId;
    private List<String> hotelImageUrls;

    // Hotel Images (Base64 + ID)
    private List<Long> hotelImageUploadBase64Id;
    private List<String> hotelImageUploadBase64;

    // Amenities (ID + Name)
    private List<Long> amenitiesId;
    private List<String> amenities;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
