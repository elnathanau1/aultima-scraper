package com.eau.AUSpider.controllers;

import com.eau.AUSpider.entities.FileEntity;
import com.eau.AUSpider.enums.FileDownloadStatus;
import com.eau.AUSpider.models.DownloadRequestModel;
import com.eau.AUSpider.models.SeriesRequestModel;
import com.eau.AUSpider.repositories.FileRepository;
import com.eau.AUSpider.services.UtilService;
import com.eau.AUSpider.services.ScraperService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DownloadController {
    private static Logger logger = LoggerFactory.getLogger(DownloadController.class);

    @Autowired
    FileRepository fileRepository;

    @Autowired
    UtilService utilService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    ScraperService scraperService;

    @PostMapping(path = "/download", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity addDownloadRequest(@RequestBody DownloadRequestModel downloadRequestModel) {
        FileEntity fileEntity = modelMapper.map(downloadRequestModel, FileEntity.class);

        FileEntity fileEntityExample = FileEntity.builder()
                .sortingFolder(fileEntity.getSortingFolder())
                .season(fileEntity.getSeason())
                .episode(fileEntity.getEpisode())
                .build();
        Example<FileEntity> example = Example.of(fileEntityExample);

        if (fileRepository.findAll(example).size() == 0) {
            fileEntity.setDownloadStatus(FileDownloadStatus.NOT_STARTED.name());

            String name = utilService.getName(fileEntity);
            fileEntity.setName(name);
            fileRepository.save(fileEntity);

            fileEntity = fileRepository.save(fileEntity);
            logger.info("Saved {}", fileEntity);
            return new ResponseEntity(fileEntity, HttpStatus.ACCEPTED);
        }
        return new ResponseEntity("Already in DB", HttpStatus.BAD_REQUEST);
    }

    @PostMapping(path = "/download/series", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity addFromSeries(@RequestBody SeriesRequestModel seriesRequestModel) {
        scraperService.addEpisodesToTable(seriesRequestModel.getUrl(), seriesRequestModel.getSortingFolder(), seriesRequestModel.getSeason(), seriesRequestModel.getPriority());
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @PostMapping(path = "/reset", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity resetStatus(@RequestBody SeriesRequestModel seriesRequestModel) {

        FileEntity fileEntityExample = FileEntity.builder()
                .sortingFolder(seriesRequestModel.getSortingFolder())
                .season(seriesRequestModel.getSeason())
                .downloadStatus(FileDownloadStatus.CANNOT_BE_SCRAPED.name())
                .build();
        Example<FileEntity> example = Example.of(fileEntityExample);

        List<FileEntity> fileEntities = fileRepository.findAll(example);
        fileEntities.forEach(x -> {
            x.setDownloadStatus(FileDownloadStatus.NOT_STARTED.name());
            fileRepository.save(x);
        });
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }
}
