package software.plusminus.tenant.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TenantException extends RuntimeException {

    public TenantException(Throwable cause) {
        super(cause);
    }

    public TenantException(String message) {
        super(message);
    }
}
