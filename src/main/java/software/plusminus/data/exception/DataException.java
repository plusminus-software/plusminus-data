package software.plusminus.data.exception;

public class DataException extends RuntimeException {

    public DataException() {
    }

    public DataException(String s) {
        super(s);
    }

    public DataException(Throwable throwable) {
        super(throwable);
    }
}
