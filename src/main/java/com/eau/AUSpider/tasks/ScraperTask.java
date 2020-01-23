package com.eau.AUSpider.tasks;


import com.eau.AUSpider.entities.FileEntity;
import com.eau.AUSpider.enums.FileDownloadStatus;
import com.eau.AUSpider.repositories.FileRepository;
import com.eau.AUSpider.services.DownloadService;
import com.eau.AUSpider.services.NameService;
import com.eau.AUSpider.services.RandomService;
import com.eau.AUSpider.services.ScraperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    NameService nameService;
//    @Value("${download.scan.task.max.files}")
//    private int maxFiles;

    @Scheduled(cron = "0 */2 * * * *")
    public void downloadFiles() throws InterruptedException {
        logger.info("Scanning for new files");
        List<FileEntity> fileEntities = fileRepository.findByDownloadStatus(FileDownloadStatus.NOT_STARTED.name());
//        Thread.sleep(randomService.getWaitTime());

        if (fileEntities.size() > 0) {
            FileEntity fileEntity = fileEntities.get(0);

            // fix name
            // can be removed later, will add name in controller
            String name = nameService.getName(fileEntity);
            fileEntity.setName(name);
            fileRepository.save(fileEntity);

            String downloadLink = scraperService.getDownloadLink(fileEntity.getUrl());
            if (downloadLink != null) {
                logger.info("Could not find download link for {}", fileEntity.getName());
                downloadService.downloadFromUrl(fileEntity, downloadLink);
            } else {
                fileEntity.setDownloadStatus(FileDownloadStatus.CANNOT_BE_SCRAPED.name());
                fileRepository.save(fileEntity);
            }
        }
    }
}
