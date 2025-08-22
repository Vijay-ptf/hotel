package com.hotel.admin_service.hotel.images;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarouselImages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carouselImageId;
    @Column(name = "carousel_image", columnDefinition = "BYTEA")
    private byte[] carouselImage;
    private String state;
}
