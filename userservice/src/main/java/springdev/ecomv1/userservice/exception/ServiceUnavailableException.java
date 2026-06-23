package springdev.ecomv1.userservice.exception;

import org.springframework.http.HttpStatus;

public class ServiceUnavailableException extends BusinessException {

    public ServiceUnavailableException(String message) {
        super(HttpStatus.SERVICE_UNAVAILABLE, message);
    }
}
