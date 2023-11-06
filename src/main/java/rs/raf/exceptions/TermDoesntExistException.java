package rs.raf.exceptions;

public class TermDoesntExistException extends RuntimeException {
    public TermDoesntExistException(String message) {
        super(message);
    }
}