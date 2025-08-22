package com.hotel.admin_service.hotel.landscape;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LandscapeService {

    @Autowired
    private LandscapeRepository landscapeRepository;

    public void addLandscapeType(Landscape landscape){
        landscapeRepository.save(landscape);
    }

    public List<Landscape> getAllLandscapes(){
       return landscapeRepository.findAll();
    }
}
