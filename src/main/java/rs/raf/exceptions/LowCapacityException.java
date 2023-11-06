package rs.raf.exceptions;

public class LowCapacityException extends RuntimeException {
    public LowCapacityException(String message) {
        super(message);
    }
}