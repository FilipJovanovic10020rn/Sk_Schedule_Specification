package rs.raf.exceptions;

public class TermTakenException extends RuntimeException {
    public TermTakenException(String message) {
        super(message);
    }
}