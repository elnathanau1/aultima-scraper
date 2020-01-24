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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
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

    @Scheduled(cron = "0 */2 * * * *")
    public void downloadFiles() throws InterruptedException {
        logger.info("Scanning for new files");
        FileEntity fileEntityExample = FileEntity.builder().downloadStatus(FileDownloadStatus.NOT_STARTED.name()).build();
        Example<FileEntity> example = Example.of(fileEntityExample);
        List<FileEntity> fileEntities = fileRepository.findAll(example, orderBy());
//        Thread.sleep(randomService.getWaitTime());

        if (fileEntities.size() > 0) {
            FileEntity fileEntity = fileEntities.get(0);

            String downloadLink = scraperService.getDownloadLink(fileEntity.getUrl());
            if (downloadLink != null) {
                downloadService.downloadFromUrl(fileEntity, downloadLink);
            } else {
                logger.info("Could not find download link for {}", fileEntity.getName());
                fileEntity.setDownloadStatus(FileDownloadStatus.CANNOT_BE_SCRAPED.name());
                fileRepository.save(fileEntity);
            }
        }
    }

    private Sort orderBy() {
        return Sort.by(Sort.Order.desc("priority"), Sort.Order.asc("episode"));
    }
}
