package com.hotel.admin_service.hotel.landscape;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel.admin_service.hotel.Hotel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Landscape {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long landscapeId;

    @NotBlank(message = "Landscape value must not be empty")
    private String landscapeTypeName;

    @OneToMany(mappedBy = "landscape", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Hotel> hotels = new ArrayList<>();

}
