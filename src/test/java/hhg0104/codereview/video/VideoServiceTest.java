package hhg0104.codereview.video;

import hhg0104.codereview.exception.AlreadyExistFileException;
import hhg0104.codereview.video.entity.VideoEntity;
import hhg0104.codereview.video.property.FileProperties;
import hhg0104.codereview.video.repository.VideoRepository;
import jakarta.persistence.EntityManagerFactory;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.mock.web.MockMultipartFile;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MockBeans({
        @MockBean(DataSource.class),
        @MockBean(EntityManagerFactory.class)
})
@SpringBootTest
public class VideoServiceTest {

    @MockBean
    private VideoRepository repo;

    @Mock
    private FileProperties fileProp;

    private VideoService service;


    @BeforeEach
    public void init() {

        String testFileDirectory = getClass().getResource("/test_file").getPath();
        when(fileProp.getUploadBasePath()).thenReturn(testFileDirectory);

        service = new VideoService(repo, fileProp);
    }

    /**
     * Test for the fetchFile method, success test.
     */
    @Test
    void testFetchFile() throws Exception {

        long testId = 1;
        String testFileName = "test.mp4";
        VideoEntity testEntity = new VideoEntity(testId, testFileName, 877127, LocalDateTime.now());

        Optional mockResult = mock(Optional.class);
        when(mockResult.orElse(null)).thenReturn(testEntity);

        when(repo.findById(testId)).thenReturn(mockResult);

        String expectPath = new File(fileProp.getUploadBasePath(), testFileName).getAbsolutePath();

        File result = service.fetchFile(testId);
        assertEquals(expectPath, result.getAbsolutePath());
    }

    /**
     * Error test for the fetchFile method, when the file info not exist in DB.
     */
    @Test
    void testFetchFileWhenFileInfoIsNull() {

        long testId = 1;

        Optional mockResult = mock(Optional.class);
        when(mockResult.orElse(null)).thenReturn(null);

        when(repo.findById(testId)).thenReturn(mockResult);

        FileNotFoundException exception = assertThrows(FileNotFoundException.class, () -> {
            service.fetchFile(testId);
        });

        assertEquals("There is no file by this file id: " + testId, exception.getMessage());
    }

    /**
     * Error test for the fetchFile method, when the file not exist in directory.
     */
    @Test
    void testFetchFileWhenFileNotExist() {

        long testId = 1;
        String testFileName = "test-not-exist.mp4";
        VideoEntity testEntity = new VideoEntity(testId, testFileName, 877127, LocalDateTime.now());

        Optional mockResult = mock(Optional.class);
        when(mockResult.orElse(null)).thenReturn(testEntity);

        when(repo.findById(testId)).thenReturn(mockResult);

        FileNotFoundException exception = assertThrows(FileNotFoundException.class, () -> {
            service.fetchFile(testId);
        });

        assertEquals("Cannot find this file: " + testFileName, exception.getMessage());
    }

    /**
     * Test for the delete method, success test.
     */
    @Test
    void testDelete() throws Exception {

        String testFileName = "test.mp4";

        File currentTestFile = new File(fileProp.getUploadBasePath(), testFileName);
        File tempCurrentTestFile = new File(fileProp.getUploadBasePath(), testFileName + ".tmp");
        FileUtils.copyFile(currentTestFile, tempCurrentTestFile);

        long testId = 1;
        VideoEntity testEntity = new VideoEntity(testId, testFileName, 877127, LocalDateTime.now());

        Optional mockResult = mock(Optional.class);
        when(mockResult.orElse(null)).thenReturn(testEntity);
        when(repo.findById(testId)).thenReturn(mockResult);
        doNothing().when(repo).delete(testEntity);

        service.delete(testId);

        assertFalse(currentTestFile.exists());

        FileUtils.moveFile(tempCurrentTestFile, currentTestFile);
    }

    /**
     * Error test for the delete method, when the file info not exist in DB.
     */
    @Test
    void testDeleteWhenFileInfoIsNull() {

        long testId = 1;

        Optional mockResult = mock(Optional.class);
        when(mockResult.orElse(null)).thenReturn(null);

        FileNotFoundException exception = assertThrows(FileNotFoundException.class, () -> {
            service.delete(testId);
        });

        assertEquals("There is no file by this file id: " + testId, exception.getMessage());
    }

    /**
     * Error test for the delete method, when the file not exist in directory.
     */
    @Test
    void testDeleteWhenFileNotExist() {

        long testId = 1;
        String testFileName = "test-not-exist.mp4";
        VideoEntity testEntity = new VideoEntity(testId, testFileName, 877127, LocalDateTime.now());

        Optional mockResult = mock(Optional.class);
        when(mockResult.orElse(null)).thenReturn(testEntity);
        when(repo.findById(testId)).thenReturn(mockResult);
        doNothing().when(repo).delete(testEntity);

        FileNotFoundException exception = assertThrows(FileNotFoundException.class, () -> {
            service.delete(testId);
        });

        assertEquals("Cannot find this file: " + testFileName, exception.getMessage());
    }

    /**
     * Test for the upload method, success test.
     */
    @Test
    void testUpload() throws Exception {

        String testNewFileName = "test-new.mp4";

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", testNewFileName, "video/mp4",
                "new test contents".getBytes());

        when(repo.save(any(VideoEntity.class))).thenReturn(null);

        File expectNewFile = new File(fileProp.getUploadBasePath(), testNewFileName);

        String newFilePath = service.upload(mockMultipartFile);

        assertEquals(expectNewFile.getAbsolutePath(), newFilePath);

        FileUtils.delete(expectNewFile);
    }

    /**
     * Erro test for the upload method, when the file already exists.
     */
    @Test
    void testUploadWhenAlreadyExistFileException() throws Exception {

        String testNewFileName = "test.mp4";

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", testNewFileName, "video/mp4",
                "new test contents".getBytes());

        AlreadyExistFileException exception = assertThrows(AlreadyExistFileException.class, () -> {
            service.upload(mockMultipartFile);
        });

        assertEquals("This file already exists: " + testNewFileName, exception.getMessage());
    }
}