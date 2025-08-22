package com.hotel.admin_service.hotel.images;

import com.hotel.admin_service.hotel.Hotel;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class HotelImageUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;
}
