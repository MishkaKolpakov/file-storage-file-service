package com.softserveacademy.java.FileService.services;

import com.softserveacademy.java.FileService.dblayer.FileStorage;
import com.softserveacademy.java.FileService.dblayer.FileStorageS3Impl;
import com.softserveacademy.java.FileService.exceptions.FileServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * this class is implementation of S3FilesTransfer
 * @see FilesService
 */
@Service
public class FilesServiceImpl implements FilesService {

    private FileStorage fileStorage;

    @Autowired
    public FilesServiceImpl(FileStorage fileStorage){
        this.fileStorage = fileStorage;
    }

    @Override
    public void saveFile(String id, MultipartFile file){
        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
            fileStorage.save(id, fileBytes);
        } catch (IOException e) {
            throw new FileServiceException();
        }
    }

    @Override
    public byte[] getFile(String id) {
        return fileStorage.load(id);
    }

    @Override
    public void deleteFile(String id) {
        fileStorage.delete(id);
    }
}
