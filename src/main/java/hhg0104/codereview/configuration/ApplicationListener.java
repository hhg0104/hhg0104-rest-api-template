package hhg0104.codereview.configuration;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class ApplicationListener implements ServletContextListener {

    public static final Logger LOG = LoggerFactory.getLogger(ApplicationListener.class);

    private String uploadBasePath;

    ApplicationListener(String uploadBasePath) {
        this.uploadBasePath = uploadBasePath;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        try {
            clearUploadDirectory();
        } catch (IOException e) {
            LOG.error("Couldn't clear the video file upload directory" + this.uploadBasePath);
        }

        ServletContextListener.super.contextInitialized(sce);
    }

    private void clearUploadDirectory() throws IOException {
        FileUtils.cleanDirectory(new File(this.uploadBasePath));
    }
}
