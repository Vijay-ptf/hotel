package com.hotel.admin_service.hotel.landscape;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface LandscapeRepository extends JpaRepository<Landscape,Long> {
    @Query("SELECT l FROM Landscape l JOIN l.hotels h WHERE h.hotelId = :hotelId")
    Landscape findByHotelId(@Param("hotelId") Long hotelId);
}
