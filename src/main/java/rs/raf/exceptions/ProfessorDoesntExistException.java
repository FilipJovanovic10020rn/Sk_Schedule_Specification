package rs.raf.exceptions;

public class ProfessorDoesntExistException extends RuntimeException {
    public ProfessorDoesntExistException(String message) {
        super(message);
    }
}