package com.softserveacademy.java.FileService.services;

import org.springframework.http.ResponseEntity;

/**
 * HttpClient interface for HttpClient service
 *
 * @version 1.0
 */

public interface HttpClient {

    ResponseEntity<String> validateToken(String token);

}
