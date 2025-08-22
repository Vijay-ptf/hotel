package com.hotel.admin_service.amenities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmenitiesRepository extends JpaRepository<Amenities, Long> {
    Amenities findByAmenitiesName(String amenitiesName);
    @Query("SELECT a FROM Amenities a JOIN a.hotels h WHERE h.hotelId = :hotelId")
    List<Amenities> findByHotelId(@Param("hotelId") Long hotelId);
}
