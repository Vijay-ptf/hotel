package com.hotel.admin_service.hotel.types;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface HotelTypeRepository extends JpaRepository<HotelTypes,Long> {
    @Query("SELECT ht FROM HotelTypes ht JOIN ht.hotels h WHERE h.hotelId = :hotelId")
    HotelTypes findByHotelId(@Param("hotelId") Long hotelId);
}
