package hhg0104.codereview.video.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class FileProperties {

    @Value("${file.upload.base}")
    private String uploadBasePath;
}
