package com.hotel.user_service.dto;

import lombok.Data;

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
    private String landscapeTypeName;
    private String hotelTypeName;
    private List<String> hotelImageUrls;
    private List<String> hotelImageUploadBase64;
    private List<String> amenities;
}
