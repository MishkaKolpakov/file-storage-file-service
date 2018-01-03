package com.softserveacademy.java.FileService.dblayer;

import com.github.fakemongo.Fongo;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFSDBFile;
import com.softserveacademy.java.FileService.exceptions.MongoException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.MongoDbFactory;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;



public class FileStorageMongoImplTest {

    private GridFsTemplate gridFsTemplate;
    private FileStorage mongoStorage;

    private static String id = "my_id";
    private static byte[] data = new byte[]{1, 2, 3, 4, 5};

    @Before
    public void setUpMongo() {
        MongoClient mongoClient = new Fongo("fongo").getMongo();
        MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(mongoClient, "fongoDB");

        MappingMongoConverter mongoConverter = new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory), new MongoMappingContext());
        gridFsTemplate = new GridFsTemplate(mongoDbFactory, mongoConverter);
        mongoStorage = new FileStorageMongoImpl(gridFsTemplate, LoggerFactory.getLogger(FileStorageMongoImpl.class));
    }


    @Test(expected = MongoException.class)
    public void save_ShouldThrowExceptionIfIdNotUnique() throws Exception {
        gridFsTemplate.store(new ByteArrayInputStream(data), id);
        mongoStorage.save(id, data);
    }

    @Test
    public void save_ShouldSaveOneFileCorrect() throws Exception {
        mongoStorage.save(id, data);

        List<GridFSDBFile> files = gridFsTemplate.find(createFindByIdQuery(id));

        assertEquals(1, files.size());

        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();

        files.get(0).writeTo(resultStream);

        assertArrayEquals(data, resultStream.toByteArray());

    }

    @Test
    public void load_ShouldReturnTheSameData() throws Exception {
        gridFsTemplate.store(new ByteArrayInputStream(data), id);
        byte[] loadedData = mongoStorage.load(id);

        assertArrayEquals(data, loadedData);
    }

    @Test(expected = MongoException.class)
    public void load_ShouldThrowEceptionIfNoData() {
        mongoStorage.load(id);
    }

    @Test
    public void delete() throws Exception {
        gridFsTemplate.store(new ByteArrayInputStream(data), id);

        mongoStorage.delete(id);

        assertTrue(gridFsTemplate.find(createFindByIdQuery(id)).isEmpty());
    }

    private Query createFindByIdQuery(String id) {
        return new Query().addCriteria(Criteria.where("filename").is(id));
    }

}