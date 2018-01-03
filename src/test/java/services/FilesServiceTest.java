package services;

import com.softserveacademy.java.FileService.dblayer.FileStorage;

import com.softserveacademy.java.FileService.services.FilesService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class FilesServiceTest {

    private FileStorage fileStorage;
    private FilesService filesService;
    private MultipartFile multipartFile;

    @Before
    public void setUp() {
        fileStorage = mock(FileStorage.class);
        filesService = mock(FilesService.class);
        multipartFile = new MockMultipartFile("1", new byte[10]);
    }

    @Test
    public void getFileTest(){
        when(filesService.getFile(anyString())).
                then(invocationOnMock ->
                        fileStorage.load(anyString())).thenReturn(any(byte[].class));

        filesService.getFile("1");
        verify(fileStorage, times(1)).load(anyString());
    }

}
