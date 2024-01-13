package hhg0104.codereview.health;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is a controller class for the health checkup
 */
@RestController
public class HealthCheckController {

    /**
     * This is a health checkup url, return 'OK' when a request comes in.
     *
     * @return "OK" text
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body("OK");
    }
}
