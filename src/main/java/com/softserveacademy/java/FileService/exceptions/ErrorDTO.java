package com.softserveacademy.java.FileService.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Miha on 16.11.2017.
 */
public class ErrorDTO {
    private HttpStatus status;
    private String message;
    private List<String> errors;

    public ErrorDTO() {
    }

    public ErrorDTO(HttpStatus status, String message, List<String> errors) {
        super();
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public ErrorDTO(HttpStatus status, String message, String error) {
        super();
        this.status = status;
        this.message = message;
        errors = Arrays.asList(error);
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
