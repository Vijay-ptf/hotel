package com.hotel.admin_service.hotel.images;

import com.hotel.admin_service.hotel.Hotel;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class HotelImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_data", columnDefinition = "BYTEA")
    private byte[] imageData;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;
}
