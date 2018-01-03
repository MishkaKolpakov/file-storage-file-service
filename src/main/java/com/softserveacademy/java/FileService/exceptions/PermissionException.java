package com.softserveacademy.java.FileService.exceptions;

/**
 * Created by Miha on 20.11.2017.
 */
public class PermissionException extends FileServiceException{

    public PermissionException() {
        super();
    }

    public PermissionException(String message) {
        super(message);
    }
}
