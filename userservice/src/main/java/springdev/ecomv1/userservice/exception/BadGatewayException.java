package springdev.ecomv1.userservice.exception;

import org.springframework.http.HttpStatus;

public class BadGatewayException extends BusinessException {

    public BadGatewayException(String message) {
        super(HttpStatus.BAD_GATEWAY, message);
    }
}
