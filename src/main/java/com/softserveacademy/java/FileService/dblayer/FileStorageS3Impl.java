package com.softserveacademy.java.FileService.dblayer;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.util.IOUtils;
import com.softserveacademy.java.FileService.exceptions.FileNotFoundException;
import com.softserveacademy.java.FileService.exceptions.S3Exception;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


public class FileStorageS3Impl implements FileStorage {

    @Autowired
    private AmazonS3 s3client;
    @Autowired
    private TransferManager transferManager;
    @Autowired
    private Logger logger;

    @Value("${jsa.s3.bucket}")
    private String bucketName;

    @Override
    public void save(String id, byte[] bytes) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(bytes.length);
        InputStream inputStream = new ByteArrayInputStream(bytes);

        Upload upload = transferManager.upload(new PutObjectRequest(bucketName, id, inputStream, objectMetadata));
        try {
            upload.waitForCompletion();
        } catch (InterruptedException e) {
            logger.error("s3 upload error");
        }

        if (!upload.isDone()) {
            throw new S3Exception("Unsuccessful file saving");
        }
    }

    @Override
    public byte[] load(String id) {
        S3Object object = s3client.getObject(bucketName, id);
        if(object != null){
            try {
                byte[] fileByteArray = IOUtils.toByteArray(object.getObjectContent());
                return fileByteArray;
            } catch (IOException e) {
                throw new S3Exception();
            }
        }
        throw new FileNotFoundException();
    }

    @Override
    public void delete(String id) {
        s3client.deleteObject(bucketName, id);
    }
}
