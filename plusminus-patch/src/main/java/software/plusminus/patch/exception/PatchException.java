package software.plusminus.patch.exception;

public class PatchException extends RuntimeException {

    public PatchException() {
    }

    public PatchException(String s) {
        super(s);
    }

    public PatchException(Throwable throwable) {
        super(throwable);
    }
}
