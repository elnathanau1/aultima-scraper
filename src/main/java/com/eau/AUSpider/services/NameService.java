package com.eau.AUSpider.services;

import com.eau.AUSpider.entities.FileEntity;
import com.eau.AUSpider.enums.MediaType;
import org.springframework.stereotype.Service;

@Service
public class NameService {

    public String getName(FileEntity fileEntity) {
        if (fileEntity.getMediaType().equals(MediaType.TV.name())) {
            return fileEntity.getSortingFolder() + "_S" + fileEntity.getSeason() + "E" + fileEntity.getEpisode();
        }
        else if (fileEntity.getMediaType().equals(MediaType.MOVIES.name())) {
            return fileEntity.getSortingFolder();
        }
        return null;
    }
}
