package com.eau.AUSpider.services;

import com.eau.AUSpider.entities.FileEntity;
import com.eau.AUSpider.enums.FileDownloadStatus;
import com.eau.AUSpider.repositories.FileRepository;
import com.github.fracpete.processoutput4j.output.ConsoleOutputProcessOutput;
import com.github.fracpete.rsync4j.RSync;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;

@Service
public class PiTransferService {
    private static Logger logger = LoggerFactory.getLogger(PiTransferService.class);

    @Value("${pi.landing.folder}")
    private String piLandingFolder;

    @Value("${local.download.path}")
    private String localDownloadPath;

    @Value("${sftp.host}")
    private String remoteHost;

    @Value("${sftp.user}")
    private String username;

    @Value("${sftp.password}")
    private String password;

    @Autowired
    private FileRepository fileRepository;

    @PostConstruct
    public void resetTransferringFiles() {
        List<FileEntity> fileEntities = fileRepository.findByDownloadStatus(FileDownloadStatus.TRANSFERRING.name());
        fileEntities.forEach(entity -> {
            entity.setDownloadStatus(FileDownloadStatus.DOWNLOADED.name());
            fileRepository.save(entity);
        });
    }

    private ChannelSftp setupJsch() throws Exception {
        JSch jsch = new JSch();
        // run 'ssh-keyscan -H -t rsa HOST_NAME >> known_hosts'
        jsch.setKnownHosts("/Users/eau/.ssh/known_hosts");

        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");

        Session jschSession = jsch.getSession(username, remoteHost);
        jschSession.setConfig(config);
        jschSession.setPassword(password);
        jschSession.connect();
        return (ChannelSftp) jschSession.openChannel("sftp");
    }

    public void sendFile(FileEntity fileEntity) {
        fileEntity.setDownloadStatus(FileDownloadStatus.TRANSFERRING.name());
        fileRepository.save(fileEntity);
        try {
            ChannelSftp channelSftp = setupJsch();
            channelSftp.connect();

            // create folders if needed
            // https://stackoverflow.com/questions/12838767/use-jsch-to-put-a-file-to-the-remote-directory-and-if-the-directory-does-not-exi
            String path = piLandingFolder + fileEntity.getFileLocation();
            String[] folders = path.split("/");
            channelSftp.cd("/");
            for (int i = 0; i < folders.length - 1; i++) {  // length - 1 so we don't create an extra folder with the name of the file
                String folder = folders[i];
                if (folder.length() > 0) {
                    try {
                        channelSftp.cd(folder);
                    } catch (SftpException e) {
                        channelSftp.mkdir(folder);
                        channelSftp.cd(folder);
                    }
                }
            }

            channelSftp.put(localDownloadPath + fileEntity.getFileLocation(), path);

            channelSftp.exit();
            fileEntity.setDownloadStatus(FileDownloadStatus.COMPLETE.name());
            fileRepository.save(fileEntity);

            File file = new File(localDownloadPath + fileEntity.getFileLocation());
            file.delete();

        } catch (Exception e) {
            fileEntity.setDownloadStatus(FileDownloadStatus.DOWNLOADED.name());
            fileRepository.save(fileEntity);
            logger.error("Error sending file={}, exception={}", fileEntity, e.getMessage(), e);
        }
    }
}
