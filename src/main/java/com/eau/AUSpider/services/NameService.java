package com.eau.AUSpider.services;

import com.eau.AUSpider.entities.FileEntity;
import org.springframework.stereotype.Service;

@Service
public class NameService {

    public String getName(FileEntity fileEntity) {
        String name = fileEntity.getSortingFolder() + "_S" + fileEntity.getSeason() + "E" + fileEntity.getEpisode();
        return name;
    }
}
