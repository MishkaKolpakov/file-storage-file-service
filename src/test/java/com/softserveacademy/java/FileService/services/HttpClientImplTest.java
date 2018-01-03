package com.softserveacademy.java.FileService.services;

import com.softserveacademy.java.FileService.FileServiceApplication;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FileServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpClientImplTest {

    private HttpClientImpl httpClient;
    private MockRestServiceServer mockServer;
    @Value("${authservice.url}")
    private String authServiceUrl;

    @Before
    public void init() {
        RestTemplate restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        httpClient = new HttpClientImpl(authServiceUrl, restTemplate);
    }

    @Test
    public void bindToShouldReturnMockRestServiceServer() throws Exception {
        assertEquals(authServiceUrl,"http://localhost:8585/");
        assertNotNull(mockServer);
    }

    @Test
    public void validateToken() throws Exception {

        mockServer.expect(requestTo(authServiceUrl + "user/validate"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        ResponseEntity<String> response = httpClient.validateToken("token");
        mockServer.verify();
        assertEquals(response.getStatusCode(), HttpStatus.OK);

    }

    @Test
    public void validateTokenFailed() throws Exception {

        mockServer.expect(requestTo(authServiceUrl + "user/validate"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest());

        ResponseEntity<String> response = httpClient.validateToken("token");
        mockServer.verify();
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);

    }

}