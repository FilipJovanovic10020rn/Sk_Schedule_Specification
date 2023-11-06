package rs.raf.exceptions;

public class WrongLectureNameException extends RuntimeException {
    public WrongLectureNameException(String message) {
        super(message);
    }
}