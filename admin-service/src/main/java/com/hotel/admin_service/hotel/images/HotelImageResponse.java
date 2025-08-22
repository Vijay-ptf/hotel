package com.hotel.admin_service.hotel.images;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HotelImageResponse {

    private Long id;
    private String base64Image;

}
