package rs.raf.exceptions;

public class WrongClassroomNameException extends RuntimeException {
    public WrongClassroomNameException(String message) {
        super(message);
    }
}