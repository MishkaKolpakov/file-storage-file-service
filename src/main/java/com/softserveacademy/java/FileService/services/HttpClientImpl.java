package com.softserveacademy.java.FileService.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpClientImpl implements HttpClient {

    private String authServiceUrl;

    @Autowired
    private Logger logger = LoggerFactory.getLogger(HttpClientImpl.class);

    private RestTemplate restTemplate;

    @Autowired
    public HttpClientImpl(@Value("${authservice.url}") String authServiceUrl, RestTemplate restTemplate) {
        this.authServiceUrl = authServiceUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public ResponseEntity<String> validateToken(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("{\"token\": \"" + token + "\"}", headers);
        try {
            return restTemplate.exchange(authServiceUrl + "user/validate", HttpMethod.POST, request, String.class);
        } catch (RestClientException e) {
            logger.info("Request to authservice failed");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
