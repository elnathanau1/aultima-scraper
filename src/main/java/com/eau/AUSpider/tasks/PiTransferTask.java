package com.eau.AUSpider.tasks;


import com.eau.AUSpider.entities.FileEntity;
import com.eau.AUSpider.enums.FileDownloadStatus;
import com.eau.AUSpider.enums.MediaType;
import com.eau.AUSpider.repositories.FileRepository;
import com.eau.AUSpider.services.PiTransferService;
import com.eau.AUSpider.services.UtilService;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.print.attribute.standard.Media;
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
    private UtilService utilService;

    @Autowired
    PiTransferService piTransferService;

    @Scheduled(cron = "0 5-50/1 * * * *")
    public void transferTVFiles() {
        FileEntity fileEntityExample = FileEntity.builder()
                .mediaType(MediaType.TV.name())
                .downloadStatus(FileDownloadStatus.DOWNLOADED.name())
                .build();
        Example<FileEntity> example = Example.of(fileEntityExample);

        List<FileEntity> fileEntities = fileRepository.findAll(example, utilService.orderBy());
        if (fileEntities.size() > 0){
            FileEntity fileEntity = fileEntities.get(0);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            logger.info("Start transferring {} {}", fileEntity.getName(), fileEntity.getFileSize());
            piTransferService.sendFile(fileEntities.get(0));
            stopWatch.stop();
            logger.info("Finished transferring {} ({}) in {} seconds", fileEntity.getName(), fileEntity.getFileSize(), stopWatch.getTime(TimeUnit.SECONDS));
        }
    }

    @Scheduled(cron = "0 5 * * * *")
    public void transferMovieFiles() {
        FileEntity fileEntityExample = FileEntity.builder()
                .mediaType(MediaType.MOVIES.name())
                .downloadStatus(FileDownloadStatus.DOWNLOADED.name())
                .build();
        Example<FileEntity> example = Example.of(fileEntityExample);

        List<FileEntity> fileEntities = fileRepository.findAll(example, utilService.orderBy());
        if (fileEntities.size() > 0){
            FileEntity fileEntity = fileEntities.get(0);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            logger.info("Start transferring {} {}", fileEntity.getName(), fileEntity.getFileSize());
            piTransferService.sendFile(fileEntities.get(0));
            stopWatch.stop();
            logger.info("Finished transferring {} ({}) in {} seconds", fileEntity.getName(), fileEntity.getFileSize(), stopWatch.getTime(TimeUnit.SECONDS));
        }
    }

}
