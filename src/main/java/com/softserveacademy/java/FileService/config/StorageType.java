package com.softserveacademy.java.FileService.config;

import com.softserveacademy.java.FileService.dblayer.FileStorage;
import com.softserveacademy.java.FileService.dblayer.FileStorageMongoImpl;
import com.softserveacademy.java.FileService.dblayer.FileStorageS3Impl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

@Configuration
public class StorageType {
    @Autowired
    private Logger logger;
    
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Profile("mongo")
    @Bean
    public FileStorage mongoFileStorage() {
        logger.debug("mongo storage");
        return new FileStorageMongoImpl(gridFsTemplate, logger);
    }

    @Profile("s3")
    @Bean
    public FileStorage s3FileStorage() {
        logger.debug("S3 storage");
        return new FileStorageS3Impl();
    }

}
