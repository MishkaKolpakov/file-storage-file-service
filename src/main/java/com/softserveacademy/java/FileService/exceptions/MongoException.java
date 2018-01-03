package com.softserveacademy.java.FileService.exceptions;

public class MongoException extends StorageException {
    public MongoException() { }

    public MongoException(String message) { super(message); }
}
