package hhg0104.codereview.configuration;

import hhg0104.codereview.video.property.FileProperties;
import jakarta.servlet.ServletContextListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class GeneralConfiguration {

    @Bean
    public ServletListenerRegistrationBean<ServletContextListener> servletListener(FileProperties fileProp) {

        ServletListenerRegistrationBean<ServletContextListener> servletListener = new ServletListenerRegistrationBean<>();
        servletListener.setListener(new ApplicationListener(fileProp.getUploadBasePath()));

        return servletListener;
    }
}
