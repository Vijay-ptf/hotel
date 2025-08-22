package com.hotel.admin_service.hotel.images;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelImageUrlResponse {

    private Long id;
    private String urls;
}
