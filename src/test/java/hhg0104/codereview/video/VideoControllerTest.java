package hhg0104.codereview.video;

import hhg0104.codereview.exception.AlreadyExistFileException;
import hhg0104.codereview.video.entity.VideoEntity;
import hhg0104.codereview.video.repository.VideoRepository;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBeans({
        @MockBean(DataSource.class),
        @MockBean(EntityManagerFactory.class),
        @MockBean(VideoRepository.class)
})
@SpringBootTest
@AutoConfigureMockMvc
public class VideoControllerTest {

    @MockBean
    private VideoService service;

    @Autowired
    private MockMvc mockMvc;

    /**
     * Test for the download API, success test.
     */
    @Test
    public void testDownloadAPI() throws Exception {

        String testFilePath = getClass().getResource("/test_file/test.mp4").getFile();
        File testFile = new File(testFilePath);

        long testFileId = 1;
        when(service.fetchFile(testFileId)).thenReturn(testFile);

        mockMvc.perform(get("/files/" + testFileId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("This is test file."));
    }

    /**
     * Error test for the download API, when cannot find the file.
     */
    @Test
    public void testDownloadAPIWhenFileNotFoundException() throws Exception {

        long testFileId = 1;
        when(service.fetchFile(testFileId))
                .thenThrow(new FileNotFoundException("Cannot find the file: " + testFileId));

        String expectContent = "{\"errorMessage\":\"Cannot find the file: 1\"}";

        mockMvc.perform(get("/files/" + testFileId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectContent));
    }

    /**
     * Error test for the download API, when file extension is not supported.
     */
    @Test
    public void testDownloadAPIWhenUnsupportedFileException1() throws Exception {

        File mockFile = mock(File.class);
        when(mockFile.getName()).thenReturn("test.txt");

        long testFileId = 1;
        when(service.fetchFile(testFileId)).thenReturn(mockFile);

        String expectContent = "{\"errorMessage\":\"[mp4, mpg4, mpg, mpeg] type files are only supported.\"}";

        mockMvc.perform(get("/files/" + testFileId))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().string(expectContent));
    }

    /**
     * Error test for the download API, when file extension is not supported.
     */
    @Test
    public void testDownloadAPIWhenUnsupportedFileException2() throws Exception {

        File mockFile = mock(File.class);
        when(mockFile.getName()).thenReturn("test.avi");

        long testFileId = 1;
        when(service.fetchFile(testFileId)).thenReturn(mockFile);

        String expectContent = "{\"errorMessage\":\"[mp4, mpg4, mpg, mpeg] type files are only supported.\"}";

        mockMvc.perform(get("/files/" + testFileId))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().string(expectContent));
    }

    /**
     * Error test for the delete API, success test.
     */
    @Test
    public void testDeleteAPI() throws Exception {

        long testFileId = 1;

        doNothing().when(service).delete(testFileId);

        mockMvc.perform(delete("/files/" + testFileId))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content().string("{}"));
    }

    /**
     * Error test for the delete API, when cannot find the file.
     */
    @Test
    public void testDeleteAPIWhenFileNotFoundException() throws Exception {

        long testFileId = 1;

        doThrow(new FileNotFoundException("Cannot find the file: " + testFileId))
                .when(service)
                .delete(testFileId);

        String expectContent = "{\"errorMessage\":\"Cannot find the file: 1\"}";

        mockMvc.perform(delete("/files/" + testFileId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(expectContent));
    }

    /**
     * Test for the upload API, when file extension is supported, mp4.
     */
    @Test
    public void testUploadAPIWhenExtensionIsMP4() throws Exception {

        String testFileName = "test.mp4";

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", testFileName, "video/mp4", "test contents".getBytes());

        String testCreatedFileLocation = "/usr/local/data/upload/" + testFileName;
        when(service.upload(mockMultipartFile)).thenReturn(testCreatedFileLocation);

        String expectContent = "{\"message\":\"File uploaded\"}";

        mockMvc.perform(multipart("/files")
                        .file(mockMultipartFile))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(expectContent))
                .andExpect(header().stringValues("Location", testCreatedFileLocation));
    }

    /**
     * Test for the upload API, when file extension is supported, mpg4.
     */
    @Test
    public void testUploadAPIWhenExtensionIsMPG4() throws Exception {

        String testFileName = "test.mpg4";

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", testFileName, "video/mpg4", "test contents".getBytes());

        String testCreatedFileLocation = "/usr/local/data/upload/" + testFileName;
        when(service.upload(mockMultipartFile)).thenReturn(testCreatedFileLocation);

        String expectContent = "{\"message\":\"File uploaded\"}";

        mockMvc.perform(multipart("/files")
                        .file(mockMultipartFile))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(expectContent))
                .andExpect(header().stringValues("Location", testCreatedFileLocation));
    }

    /**
     * Test for the upload API, when file extension is supported, mpg.
     */
    @Test
    public void testUploadAPIWhenExtensionIsMPG() throws Exception {

        String testFileName = "test.mpg";

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", testFileName, "video/mpg", "test contents".getBytes());

        String testCreatedFileLocation = "/usr/local/data/upload/" + testFileName;
        when(service.upload(mockMultipartFile)).thenReturn(testCreatedFileLocation);

        String expectContent = "{\"message\":\"File uploaded\"}";

        mockMvc.perform(multipart("/files")
                        .file(mockMultipartFile))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(expectContent))
                .andExpect(header().stringValues("Location", testCreatedFileLocation));
    }

    /**
     * Test for the upload API, when file extension is supported, mpeg.
     */
    @Test
    public void testUploadAPIWhenExtensionIsMPEG() throws Exception {

        String testFileName = "test.mpg";

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", testFileName, "video/mpg", "test contents".getBytes());

        String testCreatedFileLocation = "/usr/local/data/upload/" + testFileName;
        when(service.upload(mockMultipartFile)).thenReturn(testCreatedFileLocation);

        String expectContent = "{\"message\":\"File uploaded\"}";

        mockMvc.perform(multipart("/files")
                        .file(mockMultipartFile))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(expectContent))
                .andExpect(header().stringValues("Location", testCreatedFileLocation));
    }

    /**
     * Error test for the upload API, when file extension is not supported.
     */
    @Test
    public void testUploadAPIWhenUnsupportedFileException() throws Exception {

        String testFileName = "test.excel";

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", testFileName, "text/excel", "test contents".getBytes());

        String testCreatedFileLocation = "/usr/local/data/upload/" + testFileName;
        when(service.upload(mockMultipartFile)).thenReturn(testCreatedFileLocation);

        String expectContent = "{\"errorMessage\":\"[mp4, mpg4, mpg, mpeg] type files are only supported.\"}";

        mockMvc.perform(multipart("/files")
                        .file(mockMultipartFile))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().string(expectContent));
    }

    /**
     * Erro test for the upload API, when a file already exists.
     */
    @Test
    public void testUploadAPIWhenAlreadyExistFileException() throws Exception {

        String testFileName = "test.mpg";

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", testFileName, "video/mpg", "test contents".getBytes());

        when(service.upload(mockMultipartFile)).thenThrow(new AlreadyExistFileException("This file already exists: " + testFileName));

        String expectContent = String.format("{\"errorMessage\":\"This file already exists: %s\"}", testFileName);

        mockMvc.perform(multipart("/files")
                        .file(mockMultipartFile))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().string(expectContent));
    }

    /**
     * Error test for the upload API, when a file cannot be written.
     */
    @Test
    public void testUploadAPIWhenIOException() throws Exception {

        String testFileName = "test.mpg";

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", testFileName, "video/mpg", "test contents".getBytes());

        String testCreatedFileLocation = "/usr/local/data/upload/" + testFileName;
        when(service.upload(mockMultipartFile)).thenThrow(new IOException("This file cannot be written in this path: " + testCreatedFileLocation));

        String expectContent = String.format("{\"errorMessage\":\"This file cannot be written in this path: %s\"}", testCreatedFileLocation);

        mockMvc.perform(multipart("/files")
                        .file(mockMultipartFile))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(expectContent));
    }

    /**
     * Test for the list API, success test.
     */
    @Test
    public void testListAPI() throws Exception {

        VideoEntity video1 = new VideoEntity(1, "test1.mp4", 1827382, LocalDateTime.of(2022, 12, 11, 10, 38, 11));
        VideoEntity video2 = new VideoEntity(2, "test2.mpg4", 873892, LocalDateTime.of(2022, 11, 10, 18, 11, 29));
        VideoEntity video3 = new VideoEntity(3, "test3.mpg", 837828, LocalDateTime.of(2021, 5, 3, 7, 18, 12));
        VideoEntity video4 = new VideoEntity(4, "test4.mpeg", 229182822, LocalDateTime.of(2022, 8, 9, 20, 14, 10));

        List<VideoEntity> videos = new ArrayList<>();
        videos.add(video1);
        videos.add(video2);
        videos.add(video3);
        videos.add(video4);

        when(service.list()).thenReturn(videos);

        String expectContent = "[{\"name\":\"test1.mp4\",\"size\":1827382,\"file_id\":1,\"created_at\":\"2022-12-11 10:38:11\"}," +
                "{\"name\":\"test2.mpg4\",\"size\":873892,\"file_id\":2,\"created_at\":\"2022-11-10 06:11:29\"}," +
                "{\"name\":\"test3.mpg\",\"size\":837828,\"file_id\":3,\"created_at\":\"2021-05-03 07:18:12\"}," +
                "{\"name\":\"test4.mpeg\",\"size\":229182822,\"file_id\":4,\"created_at\":\"2022-08-09 08:14:10\"}]";

        mockMvc.perform(get("/files"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectContent));
    }

    /**
     * Error test for the list API, when an unknown exception is thrown.
     */
    @Test
    public void testListAPIWhenUnknownException() throws Exception {

        when(service.list()).thenThrow(new RuntimeException("This is a test unknown exception."));

        String expectContent = "{\"errorMessage\":\"This is a test unknown exception.\"}";

        mockMvc.perform(get("/files"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(expectContent));
    }
}