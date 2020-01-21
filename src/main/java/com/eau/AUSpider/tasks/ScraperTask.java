package com.eau.AUSpider.tasks;


import com.eau.AUSpider.entities.FileEntity;
import com.eau.AUSpider.enums.FileDownloadStatus;
import com.eau.AUSpider.repositories.FileRepository;
import com.eau.AUSpider.services.DownloadService;
import com.eau.AUSpider.services.RandomService;
import com.eau.AUSpider.services.ScraperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(value = "enable.scraping", havingValue = "true")
public class ScraperTask {

    private static Logger logger = LoggerFactory.getLogger(ScraperTask.class);

    @Autowired
    DownloadService downloadService;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    ScraperService scraperService;

    @Autowired
    RandomService randomService;

    @Value("${download.scan.task.max.files}")
    private int maxFiles;

    @Scheduled(fixedDelayString = "${download.scan.task.interval.milliseconds}")
    public void downloadFiles() throws InterruptedException {
        logger.info("Scanning for new files");
        List<FileEntity> fileEntities = fileRepository.findByDownloadStatus(FileDownloadStatus.NOT_STARTED.name());
        for (int i = 0; i < maxFiles; i++) {
            FileEntity fileEntity = fileEntities.get(i);
            if (fileEntity == null) {
                break;
            }
            Thread.sleep(randomService.getWaitTime());

            logger.info("Processing {}", fileEntity);
            String downloadLink = scraperService.getDownloadLink(fileEntity.getUrl());
            if (downloadLink != null) {
                downloadService.downloadFromUrl(fileEntity, downloadLink);
            }
        }
    }
}
