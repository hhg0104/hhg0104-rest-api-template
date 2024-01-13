package hhg0104.codereview.exception;

public class AlreadyExistFileException extends Exception{

    public AlreadyExistFileException() {
        super();
    }

    public AlreadyExistFileException(String message) {
        super(message);
    }

    public AlreadyExistFileException(String message, Exception ex) {
        super(message, ex);
    }
}
