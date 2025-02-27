package com.eau.AUSpider.services;

import com.eau.AUSpider.entities.FileEntity;
import com.eau.AUSpider.enums.FileDownloadStatus;
import com.eau.AUSpider.enums.MediaType;
import com.eau.AUSpider.repositories.FileRepository;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.asynchttpclient.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

@Service
public class DownloadService {
    private static Logger logger = LoggerFactory.getLogger(DownloadService.class);

    @Autowired
    private FileRepository fileRepository;

    @Value("${local.download.path}")
    private String localDownloadPath;

    @PostConstruct
    public void resetDownloadingFiles() {
        List<FileEntity> fileEntities = fileRepository.findByDownloadStatus(FileDownloadStatus.DOWNLOADING.name());
        fileEntities.forEach(entity -> {
            entity.setDownloadStatus(FileDownloadStatus.NOT_STARTED.name());
            fileRepository.save(entity);
        });
    }

    public void downloadFromUrl(FileEntity fileEntity, String downloadUrl) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String relativePath = "";
        String fileName = "";
        if (fileEntity.getMediaType().equals(MediaType.TV.name())) {
            relativePath = fileEntity.getMediaType() + "/"
                    + fileEntity.getSortingFolder() + "/"
                    + "Season " + fileEntity.getSeason() + "/"
                    + fileEntity.getName() + ".mp4";

            fileName = localDownloadPath + relativePath;

            fileEntity.setFileLocation(relativePath);
//            fileEntity.setDownloadStatus(FileDownloadStatus.DOWNLOADING.name());

            fileEntity.setDownloadStatus(FileDownloadStatus.SEND_TO_PI.name());
            fileEntity.setDownloadUrl(downloadUrl);
            fileRepository.save(fileEntity);
            logger.info("Sent {} to pi", fileEntity.getName());

            fileRepository.save(fileEntity);

            return;
        } else if (fileEntity.getMediaType().equals(MediaType.MOVIES.name())) {
            relativePath = fileEntity.getMediaType() + "/" + fileEntity.getName() + "/" + fileEntity.getName() + ".mp4";

            fileEntity.setFileLocation(relativePath);
            fileEntity.setDownloadStatus(FileDownloadStatus.SEND_TO_PI.name());
            fileEntity.setDownloadUrl(downloadUrl);
            fileRepository.save(fileEntity);
            logger.info("Sent {} to pi", fileEntity.getName());
            return;
        }

        try {
            File newFile = new File(fileName);
            newFile.getParentFile().mkdirs();
            newFile.createNewFile();

            logger.info("Started downloading {}", fileEntity.getName());
            AsyncHttpClient client = Dsl.asyncHttpClient();
            FileOutputStream stream = new FileOutputStream(newFile, false);

            client.prepareGet(downloadUrl).execute(new AsyncCompletionHandler<FileOutputStream>() {

                @Override
                public State onBodyPartReceived(HttpResponseBodyPart bodyPart)
                        throws Exception {
                    stream.getChannel().write(bodyPart.getBodyByteBuffer());
                    return State.CONTINUE;
                }

                @Override
                public FileOutputStream onCompleted(Response response)
                        throws Exception {
                    fileEntity.setDownloadStatus(FileDownloadStatus.DOWNLOADED.name());
                    fileEntity.setFileSize(FileUtils.byteCountToDisplaySize(FileUtils.sizeOf(newFile)));

                    fileRepository.save(fileEntity);
                    stopWatch.stop();
                    logger.info("Finished downloading {} ({}) in {} seconds", fileEntity.getName(), fileEntity.getFileSize(), stopWatch.getTime(TimeUnit.SECONDS));
                    return stream;
                }
            });
        } catch (Exception e) {
            logger.error("downloadFromUrl failed for {} with exception={}", fileEntity, e.getMessage(), e);
        }
    }

}
