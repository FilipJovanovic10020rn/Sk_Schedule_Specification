package rs.raf.exceptions;

public class ClassroomDoesntExistException extends RuntimeException{
    public ClassroomDoesntExistException(String message) {
        super(message);
    }
}
