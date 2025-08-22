package com.hotel.admin_service.hotel.images;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarouselImagesRepository extends JpaRepository<CarouselImages,Long> {

    List<CarouselImages> findByState(String state);
}
