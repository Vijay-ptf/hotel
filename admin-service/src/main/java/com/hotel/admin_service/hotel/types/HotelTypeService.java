package com.hotel.admin_service.hotel.types;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelTypeService {

    @Autowired
    private HotelTypeRepository hotelTypeRepository;

    public void addHotelType(HotelTypes hotelTypes){
        hotelTypeRepository.save(hotelTypes);
    }

    public List<HotelTypes> getAllHotelTypes(){
      return hotelTypeRepository.findAll();
    }
}
