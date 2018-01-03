package com.softserveacademy.java.FileService.exceptions;

public class S3Exception extends StorageException{
    public S3Exception() { }

    public S3Exception(String message) { super(message); }
}
