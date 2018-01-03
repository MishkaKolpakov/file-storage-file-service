package com.softserveacademy.java.FileService.api.controller;

import com.softserveacademy.java.FileService.services.FilesService;
import com.softserveacademy.java.FileService.services.HttpClient;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;

/**
 * Rest Controller to manage client requests regarding files stored on this host.
 *
 * @author user
 * @version 1.0
 * @since 2017-11-19
 */
@RestController
public class FilesApiController implements FilesApi {

    private FilesService filesService;
    private Logger logger;
    private HttpClient httpClient;

    @Autowired
    public FilesApiController(FilesService filesService, Logger logger, HttpClient httpClient) {
        this.filesService = filesService;
        this.logger = logger;
        this.httpClient = httpClient;
    }


    /**
     * Finds and returns a file specified by ID under which the file is stored on this server.
     *
     * @param fileId ID under which a file is stored on this host.
     * @return response entity consisting of resource if found and of http status
     */
    @Override
    public ResponseEntity<Resource> downloadFile(@PathVariable("fileId") String fileId, @RequestHeader(value = "X-AUTH", required = true) String X_AUTH) {
        if (httpClient.validateToken(X_AUTH).getStatusCode() == HttpStatus.OK) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            logger.info("Received request for file: {}", fileId);
            byte[] fileBytes = filesService.getFile(fileId);
            if (fileBytes != null) {
                logger.info("Received bytes from DB: {}", fileBytes);
                Resource resource = new InputStreamResource(new ByteArrayInputStream(fileBytes));
                MultiValueMap<String, String> headers = new HttpHeaders();
                headers.add("Content-Type", "application/octet-stream ");
                headers.add("Content-Disposition", String.format("attachment; filename=%s", fileId));
                return new ResponseEntity<>(resource, headers, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Finds a file stored on this server by file ID.
     *
     * @param fileId ID under which a file must be stored on this host.
     * @return response entity consisting of http status
     */
    @Override
    public ResponseEntity<Void> uploadFile(@RequestPart(value = "fileId", required = false) String fileId, @RequestPart("file") MultipartFile file, @RequestHeader(value = "X-AUTH", required = true) String X_AUTH) {
        if (httpClient.validateToken(X_AUTH).getStatusCode() == HttpStatus.OK) {
            if (file.isEmpty())
                logger.info("File is empty");
            else logger.info("File Size " + file.getSize());

            filesService.saveFile(fileId, file);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Void> deleteFileById(@PathVariable("fileId") String fileId, @RequestHeader(value = "X-AUTH", required = true) String X_AUTH) {
        logger.info("Received request for file deletion: {}", fileId);
        if (httpClient.validateToken(X_AUTH).getStatusCode() == HttpStatus.OK) {
            filesService.deleteFile(fileId);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}

