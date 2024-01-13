package hhg0104.codereview.exception;

public class UnsupportedFileException extends Exception{

    public UnsupportedFileException() {
        super();
    }

    public UnsupportedFileException(String message) {
        super(message);
    }

    public UnsupportedFileException(String message, Exception ex) {
        super(message, ex);
    }
}
