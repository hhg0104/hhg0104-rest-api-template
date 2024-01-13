package hhg0104.codereview.video.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FileInfo {

    private String fileId;

    private String name;

    private long size;

    private LocalDateTime createdAt;
}
