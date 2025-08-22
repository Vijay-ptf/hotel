package com.hotel.admin_service.hotel;

import com.hotel.admin_service.hotel.landscape.Landscape;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel,Long> {

    List<Hotel> findByDistrict(District district);
    @Query("SELECT h FROM Hotel h WHERE LOWER(h.landscape.landscapeTypeName) = LOWER(:landscapeTypeName)")
    List<Hotel> findByLandscape_LandscapeTypeName(@Param("landscapeTypeName") String landscapeTypeName);
    List<Hotel> findHotelByAdmin_AdminId(Long adminId);
    List<Hotel> findHotelByAdmin_AdminEmail(String adminEmail);

}
