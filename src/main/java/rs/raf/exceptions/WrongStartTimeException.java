package rs.raf.exceptions;

public class WrongStartTimeException extends RuntimeException {
    public WrongStartTimeException(String message) {
        super(message);
    }
}