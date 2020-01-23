package com.eau.AUSpider.repositories;

import com.eau.AUSpider.entities.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findByDownloadStatus(String downloadStatus);
    FileEntity findBySortingFolderAndSeasonAndEpisode(String sortingFolder, int season, int episode);
    List<FileEntity> findBySortingFolderAndSeasonAndDownloadStatus(String sortingFolder, int season, String downloadStatus);
}
