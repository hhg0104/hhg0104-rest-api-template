package hhg0104.codereview.video;

import hhg0104.codereview.data.ApiResponseTemplate;
import hhg0104.codereview.exception.AlreadyExistFileException;
import hhg0104.codereview.exception.UnsupportedFileException;
import hhg0104.codereview.video.entity.VideoEntity;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

/**
 * This is a controller class for handling the video files.
 */
@RestController
public class VideoController {

    private static final List<String> ACCEPT_EXTENSIONS = Arrays.asList(new String[]{"mp4", "mpg4", "mpg", "mpeg"});

    private VideoService service;


    @Autowired
    public VideoController(VideoService service) {
        this.service = service;
    }

    /**
     * Download a video file by fileid. The file name will be restored as it was when you uploaded it.
     *
     * @param fileId File id to download
     * @return
     */
    @GetMapping("/files/{fileid}")
    public ResponseEntity<Resource> download(@PathVariable(value = "fileid") int fileId) throws FileNotFoundException, UnsupportedFileException {

        File file = service.fetchFile(fileId);
        String name = file.getName();

        checkVideoFile(name);

        Resource resource;
        try {
            resource = new UrlResource(file.toURI());
        } catch (MalformedURLException e) {
            throw new FileNotFoundException("This is invalid file: " + file.getAbsolutePath());
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"")
                .body(resource);
    }

    private void checkVideoFile(String fileName) throws UnsupportedFileException {

        String extension = FilenameUtils.getExtension(fileName);
        if (!ACCEPT_EXTENSIONS.contains(extension)) {
            throw new UnsupportedFileException(ACCEPT_EXTENSIONS + " type files are only supported.");
        }
    }

    /**
     * Delete a video file.
     *
     * @param fileId File Id for a file to be deleted
     * @return
     */
    @DeleteMapping("/files/{fileid}")
    public ResponseEntity<String> delete(@PathVariable(value = "fileid") long fileId) throws IOException {

        service.delete(fileId);

        return createSuccessResponseEntity(null, HttpStatus.NO_CONTENT, null);
    }

    private ResponseEntity<String> createSuccessResponseEntity(String message, HttpStatus httpStatus, HttpHeaders headers) {

        if (headers == null) {
            headers = new HttpHeaders();
        }

        String successMessage = ApiResponseTemplate.builder()
                .message(message)
                .build()
                .toJson();

        return ResponseEntity
                .status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers)
                .body(successMessage);
    }

    /**
     * Upload a video file.
     *
     * @param file File to be uploaded
     * @return File create success message
     */
    @PostMapping(value = "/files")
    public ResponseEntity<String> upload(@RequestParam(value = "file") MultipartFile file) throws UnsupportedFileException,
            IOException, AlreadyExistFileException {

        String fileName = file.getOriginalFilename();
        checkVideoFile(fileName);

        String createdFileLocation = service.upload(file);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", createdFileLocation);

        return createSuccessResponseEntity("File uploaded", HttpStatus.CREATED, headers);
    }

    /**
     * List uploaded files.
     *
     * @return Uploaded file list
     */
    @GetMapping("/files")
    public List<VideoEntity> list() {
        return service.list();
    }
}
