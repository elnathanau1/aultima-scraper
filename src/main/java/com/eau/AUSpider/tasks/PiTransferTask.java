package com.eau.AUSpider.tasks;


import com.eau.AUSpider.entities.FileEntity;
import com.eau.AUSpider.enums.FileDownloadStatus;
import com.eau.AUSpider.repositories.FileRepository;
import com.eau.AUSpider.services.PiTransferService;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(value = "enable.transfer", havingValue = "true")
public class PiTransferTask {

    private static Logger logger = LoggerFactory.getLogger(PiTransferTask.class);

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    PiTransferService piTransferService;

//    @Scheduled(fixedDelayString = "${download.scan.task.interval.milliseconds}")
    @Scheduled(cron = "0 5-50/5 * * * *")
    public void transferFiles() {
        List<FileEntity> fileEntities = fileRepository.findByDownloadStatus(FileDownloadStatus.DOWNLOADED.name());
        if (fileEntities.size() > 0){
            FileEntity fileEntity = fileEntities.get(0);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            logger.info("1: Start transferring {} ({})", fileEntity.getName(), fileEntity.getFileSize());
            piTransferService.sendFile(fileEntities.get(0));
            stopWatch.stop();
            logger.info("1: Finished transferring {} ({}) in ({}) seconds", fileEntity.getName(), fileEntity.getFileSize(), stopWatch.getTime(TimeUnit.SECONDS));
        }
    }

    @Scheduled(cron = "30 5-50/5 * * * *")
    public void transferFiles2() {
        List<FileEntity> fileEntities = fileRepository.findByDownloadStatus(FileDownloadStatus.DOWNLOADED.name());
        if (fileEntities.size() > 0) {
            FileEntity fileEntity = fileEntities.get(0);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            logger.info("2: Start transferring {} ({})", fileEntity.getName(), fileEntity.getFileSize());
            piTransferService.sendFile(fileEntities.get(0));
            stopWatch.stop();
            logger.info("2: Finished transferring {} ({}) in ({}) seconds", fileEntity.getName(), fileEntity.getFileSize(), stopWatch.getTime(TimeUnit.SECONDS));
        }
    }
}
