package com.eau.AUSpider.config;

import com.eau.AUSpider.entities.FileEntity;
import com.eau.AUSpider.models.DownloadRequestModel;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    private static Logger logger = LoggerFactory.getLogger(ModelMapperConfig.class);

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(DownloadRequestModel.class, FileEntity.class).addMappings(mapping -> {
            mapping.skip(FileEntity::setId);
            mapping.skip(FileEntity::setName);
            mapping.skip(FileEntity::setCreateTimestampUtc);
            mapping.skip(FileEntity::setUpdateTimestampUtc);
            mapping.skip(FileEntity::setDownloadStatus);
            mapping.skip(FileEntity::setFileLocation);
            mapping.skip(FileEntity::setFileSize);
            mapping.skip(FileEntity::setDownloadUrl);
        });

        return modelMapper;
    }
}