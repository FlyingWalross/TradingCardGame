package http;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public enum HttpStatus {
    OK(200, "OK"),
    CREATED(201, "CREATED"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    DUPLICATE(409, "Already exists"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NO_CONTENT(204, "No Content");

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private int code;
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private String message;

    HttpStatus(int code, String message) {
        setCode(code);
        setMessage(message);
    }
}
