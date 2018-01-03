package com.softserveacademy.java.FileService.api.controller;

import com.softserveacademy.java.FileService.FileServiceApplication;
import com.softserveacademy.java.FileService.exceptions.ErrorDTO;
import com.softserveacademy.java.FileService.exceptions.FileServiceException;
import com.softserveacademy.java.FileService.exceptions.PermissionException;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import static io.restassured.RestAssured.given;
import static junit.framework.TestCase.assertSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Miha on 16.11.2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FileServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ApplicationExceptionHandlerTest {

    @LocalServerPort
    private int serverPort;

    private ResponseEntityExceptionHandler exceptionHandlerSupport;

    private DefaultHandlerExceptionResolver defaultExceptionResolver;

    private WebRequest request;

    private MockHttpServletRequest servletRequest;

    private MockHttpServletResponse servletResponse;

    private ExceptionHandlerExceptionResolver resolver;

    @Before
    public void setup() {
        this.servletRequest = new MockHttpServletRequest("GET", "/");
        this.servletResponse = new MockHttpServletResponse();
        this.request = new ServletWebRequest(this.servletRequest, this.servletResponse);

        this.exceptionHandlerSupport = new ApplicationExceptionHandler();
        this.defaultExceptionResolver = new DefaultHandlerExceptionResolver();

        StaticWebApplicationContext cxt = new StaticWebApplicationContext();
        cxt.registerSingleton("exceptionHandler", ApplicationExceptionHandler.class);
        cxt.refresh();

        resolver = new ExceptionHandlerExceptionResolver();
        resolver.setApplicationContext(cxt);
        resolver.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        resolver.afterPropertiesSet();
    }

    @Value("${application.url}")
    private String URL_PREFIX;

    @Test
    public void handleAll() throws Exception {
        Exception ex = new Exception();
        testException(ex);
    }

    @Test
    public void handleMissingServletRequestPart() throws Exception {
        Exception ex = new MissingServletRequestPartException("partName");
        testException(ex);
    }

    @Test
    public void handleMissingServletRequestParameter() throws Exception {
        Exception ex = new MissingServletRequestParameterException("parameterName"," title");
        testException(ex);
    }
    @Mock
    private
    MethodParameter methodParameter;
    @Mock
    private
    BindingResult bindingResult;

    @Ignore
    @Test
    public void handleMethodArgumentNotValid() throws Exception {
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);
        resolver.resolveException(this.servletRequest, this.servletResponse, null, ex);

        assertEquals(400, this.servletResponse.getStatus());
    }

    @Test
    public void handleHttpRequestMethodNotSupported() throws Exception {
        RestAssured.defaultParser = Parser.JSON;
        final Response response = given().contentType("multipart/form-data;charset=UTF-8").put("http://localhost:" + serverPort +"/files/1");

        final ErrorDTO error = response.as(ErrorDTO.class);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, error.getStatus());
        assertEquals(1, error.getErrors().size());
        assertTrue(error.getErrors().get(0).contains("Supported methods are"));
    }

    // What should I set in request url to generate MethodArgumentTypeMismatchException if argument must beString.class
    @Ignore
    @Test
    public void handleMethodArgumentTypeMismatch() throws Exception {

        RestAssured.defaultParser = Parser.JSON;
        Response response = given().contentType("multipart/form-data;charset=UTF-8").get(URL_PREFIX + "files/");
        ErrorDTO error = response.as(ErrorDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatus());
        assertEquals(1, error.getErrors().size());
        assertTrue(error.getErrors().get(0).contains("should be of type"));
    }

    @Test
    public void handleHttpMediaTypeNotSupported() throws Exception {
        RestAssured.defaultParser = Parser.JSON;
        Response response = given().post("http://localhost:" + serverPort + "/files/ ");
        ErrorDTO error = response.as(ErrorDTO.class);

        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, error.getStatus());
        assertEquals(1, error.getErrors().size());
        assertTrue(error.getErrors().get(0).contains("media type is not supported"));
    }

    @Test
    public void handleFileServiceException() throws Exception {

        FileServiceException ex = new FileServiceException("message");
        resolver.resolveException(this.servletRequest, this.servletResponse, null, ex);

        assertEquals(400, this.servletResponse.getStatus());
    }

    @Ignore
    @Test
    public void handlePermissionException() throws Exception {
        PermissionException ex = new PermissionException("message");
        resolver.resolveException(this.servletRequest, this.servletResponse, null, ex);

        assertEquals(403, this.servletResponse.getStatus());
    }

    @Test
    public void controllerAdvice() throws Exception {

        ServletRequestBindingException ex = new ServletRequestBindingException("message");
        resolver.resolveException(this.servletRequest, this.servletResponse, null, ex);

        assertEquals(400, this.servletResponse.getStatus());
    }

    private ResponseEntity<Object> testException(Exception ex) {
        ResponseEntity<Object> responseEntity = this.exceptionHandlerSupport.handleException(ex, this.request);

        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(responseEntity.getStatusCode())) {
            assertSame(ex, this.servletRequest.getAttribute("javax.servlet.error.exception"));
            return responseEntity;
        }

        this.defaultExceptionResolver.resolveException(this.servletRequest, this.servletResponse, null, ex);

        assertEquals(this.servletResponse.getStatus(), responseEntity.getStatusCode().value());

        return responseEntity;
    }


}