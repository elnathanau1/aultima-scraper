package com.eau.AUSpider.services;

import com.eau.AUSpider.entities.FileEntity;
import com.eau.AUSpider.enums.FileDownloadStatus;
import com.eau.AUSpider.repositories.FileRepository;
import org.asynchttpclient.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

@Service
public class DownloadService {
    private static Logger logger = LoggerFactory.getLogger(DownloadService.class);

    @Autowired
    private FileRepository fileRepository;

    @Value("${external.drive.path}")
    private String externalDrivePath;

    @PostConstruct
    public void resetDownloadingFiles() {
        List<FileEntity> fileEntities = fileRepository.findByDownloadStatus(FileDownloadStatus.DOWNLOADING.name());
        fileEntities.forEach(entity -> {
            entity.setDownloadStatus(FileDownloadStatus.NOT_STARTED.name());
            fileRepository.save(entity);
        });
    }

    public void downloadFromUrl(FileEntity fileEntity, String downloadUrl) {
        String fileName = externalDrivePath + fileEntity.getMediaType() + "/" + fileEntity.getSortingFolder() + "/" + fileEntity.getName() + ".mp4";

        fileEntity.setFileLocation(fileName);
        fileEntity.setDownloadStatus(FileDownloadStatus.DOWNLOADING.name());
        fileRepository.save(fileEntity);

        try {
            File newFile = new File(fileName);
            newFile.getParentFile().mkdirs();
            newFile.createNewFile();

            logger.info("Started downloading fileEntity={}", fileEntity);
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
                    fileEntity.setDownloadStatus(FileDownloadStatus.COMPLETE.name());
                    fileRepository.save(fileEntity);
                    logger.info("Finished downloading fileEntity={}", fileEntity);
                    return stream;
                }
            });
        }
        catch (Exception e) {
            logger.error("downloadFromUrl failed for {} with exception={}", fileEntity, e.getMessage(), e);
        }
    }

}
