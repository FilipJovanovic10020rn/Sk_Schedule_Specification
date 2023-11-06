package rs.raf.exceptions;

public class SameNameException extends RuntimeException {
    public SameNameException(String message) {
        super(message);
    }
}