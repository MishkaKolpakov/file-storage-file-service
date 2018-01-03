package com.softserveacademy.java.FileService.exceptions;

/**
 * Created by Miha on 14.11.2017.
 */
public class FileNotFoundException extends FileServiceException {

    public FileNotFoundException() {
    }

    public FileNotFoundException(String message) {
        super(message);
    }
}
