package com.hotel.admin_service.hotel;

import com.hotel.admin_service.admin.Admin;
import com.hotel.admin_service.amenities.Amenities;
import com.hotel.admin_service.hotel.images.HotelImage;
import com.hotel.admin_service.hotel.images.HotelImageUrl;
import com.hotel.admin_service.hotel.landscape.Landscape;
import com.hotel.admin_service.hotel.types.HotelTypes;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hotelId;

    @NotBlank(message = "Hotel name is required")
    private String hotelName;

    @NotBlank(message = "Hotel description is required")
    @Size(max = 1000, message = "Description should not exceed 1000 characters")
    private String hotelDescription;

    @DecimalMin(value = "0.0", inclusive = true)
    @DecimalMax(value = "5.0", inclusive = true)
    private Double hotelRating;

    @NotBlank(message = "Basic price is required")
    private String hotelBasicPricePerNight;

    @NotBlank(message = "Hotel address is required")
    private String hotelAddress;

    @Email
    private String hotelEmail;

    @Pattern(regexp = "^\\d{10}$", message = "Phone number must be 10 digits")
    private String hotelPhoneNumber;

    @Enumerated(EnumType.STRING)
    private District district;

    @Column(columnDefinition = "TEXT")
    private String location;

    @ManyToOne
    @JoinColumn(name = "landscape_id")
    private Landscape landscape;

    @ManyToOne
    @JoinColumn(name = "hotel_types_id")
    private HotelTypes hotelTypes;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HotelImage> hotelImageUpload = new ArrayList<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HotelImageUrl> hotelImageUrls = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "hotel_amenities_list",
            joinColumns = @JoinColumn(name = "hotel_id"),
            inverseJoinColumns = @JoinColumn(name = "amenities_id")
    )
    private List<Amenities> amenitiesList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}