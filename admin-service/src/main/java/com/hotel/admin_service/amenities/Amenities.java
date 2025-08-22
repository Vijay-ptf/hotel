package com.hotel.admin_service.amenities;

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
public class Amenities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long amenitiesId;
    private String amenitiesName;

    @ManyToMany(mappedBy = "amenitiesList")
    @JsonIgnore
    private List<Hotel> hotels = new ArrayList<>();

}
