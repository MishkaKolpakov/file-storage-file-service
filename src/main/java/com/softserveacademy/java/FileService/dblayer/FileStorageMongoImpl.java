package com.softserveacademy.java.FileService.dblayer;

import com.mongodb.gridfs.GridFSDBFile;
import com.softserveacademy.java.FileService.exceptions.FileServiceException;
import com.softserveacademy.java.FileService.exceptions.MongoException;
import org.slf4j.Logger;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * this class is implementation of FileStorage class using MongoDB as storage
 * @see FileStorage
 */
public class FileStorageMongoImpl implements FileStorage {

    private GridFsTemplate gridFsTemplate;
    private Logger logger;

    @Autowired
    public FileStorageMongoImpl(GridFsTemplate gridFsTemplate, Logger logger) {
        this.gridFsTemplate = gridFsTemplate;
        this.logger = logger;
    }

    @Override
    public void save(String id, byte[] bytes) {
        logger.debug("Storing file into MongoDB");

        if (!gridFsTemplate.find(createFindByIdQuery(id)).isEmpty()) {
            MongoException mongoException = new MongoException("File with specified id already exist");
            logger.error(mongoException.getMessage(), mongoException);
            throw mongoException;
        }
        gridFsTemplate.store(new ByteArrayInputStream(bytes), id);
    }

    @Override
    public byte[] load(String id) {
        logger.debug("Loading file from MongoDB");

        GridFSDBFile file = gridFsTemplate.findOne(createFindByIdQuery(id));

        if (file == null ) {
            MongoException mongoException = new MongoException(String.format("File with id %s does not exist in MongoDB", id));
            logger.error(mongoException.getMessage(), mongoException);
            throw mongoException;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            file.writeTo(byteArrayOutputStream);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new MongoException("Can't load file");
        }

        return byteArrayOutputStream.toByteArray();
    }

    @Override

    public void delete(String id) {
        logger.debug("Deleting file with id " + id);
        gridFsTemplate.delete(createFindByIdQuery(id));
    }

    private Query createFindByIdQuery(String id) {
        return new Query().addCriteria(Criteria.where("filename").is(id));
    }
}

