package com.hotel.admin_service.amenities;

import com.hotel.admin_service.hotel.Hotel;
import com.hotel.admin_service.hotel.HotelRepository;
import com.hotel.admin_service.security.exception.AmenitiesExistException;
import com.hotel.admin_service.security.exception.NoHotelsFoundException;
import com.hotel.admin_service.security.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AmenitiesService {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private AmenitiesRepository amenitiesRepository;

    public Amenities addAmenitiesToHotel(Amenities amenities) {
        Amenities isExist=amenitiesRepository.findByAmenitiesName(amenities.getAmenitiesName());
        if(isExist!=null){
            throw new AmenitiesExistException("Amenities already exist"+" "+amenities.getAmenitiesName());
        }
        amenitiesRepository.save(amenities);
        return amenities;
    }

    public List<Amenities> getAllAmenities(){
        return amenitiesRepository.findAll();
    }

    @Transactional
    public String assignAmenitiesToHotel(AmenitiesRequest request) {
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new NoHotelsFoundException("Hotel not found with ID: " + request.getHotelId()));

        List<Amenities> amenitiesToAssign = amenitiesRepository.findAllById(request.getAmenitiesIds());

        List<Amenities> currentAmenities = hotel.getAmenitiesList();
        for (Amenities amenity : amenitiesToAssign) {
            if (!currentAmenities.contains(amenity)) {
                currentAmenities.add(amenity);
            }
        }

        hotel.setAmenitiesList(currentAmenities);
        hotelRepository.save(hotel);

        return "Amenities successfully assigned to hotel ID: " + hotel.getHotelId();
    }

    @Transactional
    public void deleteAmenities(Long hotelId, Long amenitiesId) {

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new NoHotelsFoundException("Hotel Not Found with ID: " + hotelId));

        Amenities amenities = amenitiesRepository.findById(amenitiesId)
                .orElseThrow(() -> new ResourceNotFoundException("Amenities Not Found with ID: " + amenitiesId));

        hotel.getAmenitiesList().remove(amenities);

        hotelRepository.save(hotel);
    }

    public void deleteAddedAmenities(Long amenitiesId){
        Amenities amenities = amenitiesRepository.findById(amenitiesId)
                .orElseThrow(() -> new ResourceNotFoundException("Amenities Not Found with ID: " + amenitiesId));
        amenitiesRepository.deleteById(amenitiesId);
    }

    public void editAmenitiesName(Long amenitiesId,String newName){
        Amenities amenities = amenitiesRepository.findById(amenitiesId)
                .orElseThrow(() -> new ResourceNotFoundException("Amenities Not Found with ID: " + amenitiesId));
        amenities.setAmenitiesName(newName);
        amenitiesRepository.save(amenities);
    }

    public List<AmenitiesRequest> getAmenitiesByHotelId(Long hotelId) {
        List<Amenities> amenitiesList = amenitiesRepository.findByHotelId(hotelId);
        List<Long> amenitiesIds = amenitiesList.stream()
                .map(Amenities::getAmenitiesId)
                .toList();

        AmenitiesRequest request = new AmenitiesRequest(hotelId, amenitiesIds);
        return List.of(request);
    }

}
