package rs.raf.exceptions;

public class ClassLectureDoesntExistException extends RuntimeException{
    public ClassLectureDoesntExistException(String message) {
        super(message);
    }
}
