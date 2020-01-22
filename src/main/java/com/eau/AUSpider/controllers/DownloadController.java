package com.eau.AUSpider.controllers;

import com.eau.AUSpider.entities.FileEntity;
import com.eau.AUSpider.enums.FileDownloadStatus;
import com.eau.AUSpider.models.DownloadRequestModel;
import com.eau.AUSpider.repositories.FileRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DownloadController {
    private static Logger logger = LoggerFactory.getLogger(DownloadController.class);

    @Autowired
    FileRepository fileRepository;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping(path = "/download/", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity addDownloadRequest(@RequestBody DownloadRequestModel downloadRequestModel) {
        FileEntity fileEntity = modelMapper.map(downloadRequestModel, FileEntity.class);
        fileEntity.setDownloadStatus(FileDownloadStatus.NOT_STARTED.name());
        fileEntity = fileRepository.save(fileEntity);
        logger.info("Saved {}", fileEntity);
        return new ResponseEntity(fileEntity, HttpStatus.ACCEPTED);
    }
}
