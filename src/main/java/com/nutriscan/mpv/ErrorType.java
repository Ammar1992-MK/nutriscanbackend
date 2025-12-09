package com.nutriscan.mpv;

import org.springframework.http.HttpStatus;

public enum ErrorType {
    BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED, "The username or password is incorrect"),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "The account is locked"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "You are not authorized to access this resource"),
    INVALID_JWT(HttpStatus.FORBIDDEN, "The JWT signature is invalid"),
    JWT_EXPIRED(HttpStatus.FORBIDDEN, "The JWT token has expired"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown internal server error");

    private final HttpStatus status;
    private final String message;

    ErrorType(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
