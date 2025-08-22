package com.hotel.admin_service.hotel.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel.admin_service.hotel.Hotel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelTypes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hotelTypeId;

    private String hotelTypeName;

    @OneToMany(mappedBy = "hotelTypes", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Hotel> hotels = new ArrayList<>();

}
