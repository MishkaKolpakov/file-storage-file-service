package com.softserveacademy.java.FileService.exceptions;

/**
 * Created by Miha on 14.11.2017.
 */
public class FileServiceException extends RuntimeException{
    public FileServiceException() {
        super();
    }

    public FileServiceException(String message) {
        super(message);
    }
}
