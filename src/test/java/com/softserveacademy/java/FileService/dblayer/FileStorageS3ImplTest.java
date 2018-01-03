package com.softserveacademy.java.FileService.dblayer;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Ignore
public class FileStorageS3ImplTest {

    private static FileStorageS3Impl fileStorageS3;
    private static Upload upload;
    private static TransferManager transferManager;
    private static AmazonS3 amazonS3;

    @Before
    public void init(){
        upload = mock(Upload.class);
        transferManager = mock(TransferManager.class);
        amazonS3 = mock(AmazonS3.class);
        when(transferManager.upload(any(PutObjectRequest.class))).thenReturn(upload);
        when(upload.isDone()).thenReturn(true);

        //fileStorageS3 = new FileStorageS3Impl(amazonS3, transferManager);

    }

    @Test
    public void successUpload(){
        fileStorageS3.save("link", new byte[10]);

        verify(transferManager).upload(any(PutObjectRequest.class));
        verify(upload).isDone();
    }

    @Test
    public void failUpload(){
        fileStorageS3.save("link", new byte[10]);

        verify(transferManager).upload(any(PutObjectRequest.class));
        verify(upload).isDone();
    }

}
