package software.plusminus.data.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ClientDataException extends RuntimeException {

    public ClientDataException() {
    }

    public ClientDataException(String s) {
        super(s);
    }

    public ClientDataException(Throwable throwable) {
        super(throwable);
    }
}
