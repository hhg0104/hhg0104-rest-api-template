package hhg0104.codereview.video;

import hhg0104.codereview.exception.AlreadyExistFileException;
import hhg0104.codereview.video.entity.VideoEntity;
import hhg0104.codereview.video.property.FileProperties;
import hhg0104.codereview.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * This is a service class to handle the uploading and downloading for video file.
 */
@Component
public class VideoService {

    private VideoRepository repository;

    private FileProperties fileProp;

    @Autowired
    public VideoService(VideoRepository repository, FileProperties fileProp) {
        this.repository = repository;
        this.fileProp = fileProp;
    }

    /**
     * Get existing video file.
     *
     * @param fileId File ID
     * @return Existing video file.
     * @throws FileNotFoundException File not exists exception
     */
    public File fetchFile(long fileId) throws FileNotFoundException {

        VideoEntity info = fetchFileInfoFromDB(fileId);

        return convertToFile(info);
    }

    private VideoEntity fetchFileInfoFromDB(long fileId) throws FileNotFoundException {

        VideoEntity info = repository.findById(fileId).orElse(null);
        if (info == null) {
            throw new FileNotFoundException("There is no file by this file id: " + fileId);
        }

        return info;
    }

    private File convertToFile(VideoEntity info) throws FileNotFoundException {

        String fileName = info.getName();
        File file = new File(fileProp.getUploadBasePath(), fileName);
        if (!file.exists()) {
            throw new FileNotFoundException("Cannot find this file: " + fileName);
        }

        return file;
    }

    /**
     * Delete an existing file.
     *
     * @param fileId File ID
     * @throws FileNotFoundException File not exists exception
     * @throws IOException           File delete fail exception
     */
    @Transactional
    public void delete(long fileId) throws FileNotFoundException, IOException {

        VideoEntity info = fetchFileInfoFromDB(fileId);

        repository.delete(info);

        File file = convertToFile(info);
        boolean deleted = file.delete();
        if (!deleted) {
            throw new IOException("This file cannot be deleted. :" + file.getName());
        }
    }

    /**
     * Upload a new video file.
     *
     * @param file New video file
     * @return Created file location
     * @throws IOException               File upload exception
     * @throws AlreadyExistFileException File already exist exception
     */
    @Transactional
    public String upload(MultipartFile file) throws IOException, AlreadyExistFileException {

        String fileName = file.getOriginalFilename();
        File newFile = new File(fileProp.getUploadBasePath(), fileName);
        if (newFile.exists()) {
            throw new AlreadyExistFileException("This file already exists: " + fileName);
        }

        long size = file.getSize();
        insertInfo(fileName, size);

        Path uploadFilePath = Path.of(newFile.getAbsolutePath());
        Files.copy(file.getInputStream(), uploadFilePath);

        return newFile.getAbsolutePath();
    }

    private void insertInfo(String newFileName, long size) {
        VideoEntity newVideo = new VideoEntity();
        newVideo.setName(newFileName);
        newVideo.setSize(size);

        repository.save(newVideo);
    }

    /**
     * Return all video file list.
     *
     * @return All video file list
     */
    public List<VideoEntity> list() {
        return repository.findAll();
    }
}
