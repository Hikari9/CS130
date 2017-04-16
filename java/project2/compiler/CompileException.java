package project2.compiler;

public class CompileException extends Exception {

    public CompileException(String message) {
        super(message);
    }

    public CompileException(String message, Throwable error) {
        super(message, error);
    }

}
