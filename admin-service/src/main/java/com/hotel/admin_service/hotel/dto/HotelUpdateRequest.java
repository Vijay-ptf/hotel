package com.hotel.admin_service.hotel.dto;

import lombok.Data;

import java.util.List;

@Data
public class HotelUpdateRequest {
    private String hotelName;
    private String hotelDescription;
    private Double hotelRating;
    private String hotelBasicPricePerNight;
    private String hotelAddress;
    private String hotelEmail;
    private String hotelPhoneNumber;
    private String district;
    private String location;
    private Long landscapeId;
    private Long hotelTypeId;
}
