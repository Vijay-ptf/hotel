package com.hotel.admin_service.hotel.images;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelImageUrlRepository extends JpaRepository<HotelImageUrl, Long> {
    List<HotelImageUrl> findByHotel_HotelId(Long hotelId);
}
