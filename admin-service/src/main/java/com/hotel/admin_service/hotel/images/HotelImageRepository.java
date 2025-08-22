package com.hotel.admin_service.hotel.images;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelImageRepository extends JpaRepository<HotelImage, Long> {

    List<HotelImage> findByHotel_HotelId(Long hotelId);
}
