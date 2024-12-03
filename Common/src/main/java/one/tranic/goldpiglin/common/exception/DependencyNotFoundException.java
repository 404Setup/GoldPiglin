package one.tranic.goldpiglin.common.exception;

public class DependencyNotFoundException extends RuntimeException {
    public DependencyNotFoundException() {
        super();
    }

    public DependencyNotFoundException(String message) {
        super(message);
    }
}
