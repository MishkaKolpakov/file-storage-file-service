package com.softserveacademy.java.FileService.api.controller;

import com.softserveacademy.java.FileService.FileServiceApplication;
import com.softserveacademy.java.FileService.services.FilesService;
import com.softserveacademy.java.FileService.services.HttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {FileServiceApplication.class})
@AutoConfigureMockMvc
public class FilesApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilesService filesService;
    @MockBean
    private HttpClient httpClient;
    @Autowired
    private Logger logger;

    private MockMultipartFile multipartFile;
    private String fileId;
    private String token;

    @Before
    public void setUp() {
        multipartFile = new MockMultipartFile("file", "12345", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        fileId = "12345";
        token = "token";
    }

    @Test
    public void downloadFile_ReturnHttpSuccessStatusAndFileContent_IfFileIsFound() throws Exception {
        byte[] fileBytes = multipartFile.getBytes();
        given(filesService.getFile(fileId)).willReturn(fileBytes);
        given(httpClient.validateToken(token)).willReturn(new ResponseEntity<>(HttpStatus.OK));

        RequestBuilder requestBuilder = get(String.format("/files/%s", fileId))
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .header("X-AUTH", token);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(content().bytes(fileBytes));
    }

    @Test
    public void downloadFile_ReturnHttpClientErrorStatusAndNoFileContent_IfFileIsNotFound() throws Exception {
        given(filesService.getFile(fileId)).willReturn(null);
        given(httpClient.validateToken(token)).willReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        RequestBuilder requestBuilder = get(String.format("/files/%s", fileId))
                .header("X-AUTH", token);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(content().string(""));
    }

    @Test
    public void uploadFile_ReturnHttpSuccessStatusAndNoContent_IfFileIsSaved() throws Exception {
        given(httpClient.validateToken(token)).willReturn(new ResponseEntity<>(HttpStatus.OK));
        MockMultipartFile fileIdMultipartFile = new MockMultipartFile
                ("fileId", "fileId", MediaType.TEXT_PLAIN_VALUE, fileId.getBytes());

        RequestBuilder requestBuilder = fileUpload("/files")
                .file(multipartFile)
                .file(fileIdMultipartFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("X-AUTH", token);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(content().string(""));
    }

}