package com.eau.AUSpider.tasks;


import com.eau.AUSpider.entities.FileEntity;
import com.eau.AUSpider.enums.FileDownloadStatus;
import com.eau.AUSpider.repositories.FileRepository;
import com.eau.AUSpider.services.PiTransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
@ConditionalOnProperty(value = "enable.transfer", havingValue = "true")
public class PiTransferTask {

    private static Logger logger = LoggerFactory.getLogger(PiTransferTask.class);

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    PiTransferService piTransferService;

    @Scheduled(fixedDelayString = "${download.scan.task.interval.milliseconds}")
    public void transferFiles() {
        logger.info("Start file transfer");
        List<FileEntity> fileEntities = fileRepository.findByDownloadStatus(FileDownloadStatus.DOWNLOADED.name());
        for (FileEntity fileEntity : fileEntities) {
            logger.info("Transferring {}", fileEntity);
            piTransferService.sendFile(fileEntity);
            logger.info("Finished transferring {}", fileEntity);
        }
        logger.info("Finish file transfer");
    }
}
