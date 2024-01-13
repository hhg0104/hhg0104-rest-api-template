package hhg0104.codereview.configuration;

import hhg0104.codereview.data.ApiResponseTemplate;
import hhg0104.codereview.exception.AlreadyExistFileException;
import hhg0104.codereview.exception.UnsupportedFileException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.FileNotFoundException;

@ControllerAdvice
public class ExceptionHandleAdvice {

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<String> handleBindException(FileNotFoundException ex) {
        return createErrorResponseEntity(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnsupportedFileException.class)
    public ResponseEntity<String> handleUnsupportedFileException(UnsupportedFileException ex) {
        return createErrorResponseEntity(ex.getMessage(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(AlreadyExistFileException.class)
    public ResponseEntity<String> handleAlreadyExistFileException(AlreadyExistFileException ex) {
        return createErrorResponseEntity(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return createErrorResponseEntity(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<String> createErrorResponseEntity(String errorMessage, HttpStatus httpStatus) {
        String errorJson = ApiResponseTemplate.builder()
                .errorMessage(errorMessage)
                .build()
                .toJson();

        return ResponseEntity
                .status(httpStatus)
                .body(errorJson);
    }
}
