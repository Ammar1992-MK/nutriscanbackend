package com.nutriscan.mpv.exceptions;

import com.nutriscan.mpv.ErrorType;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 401 Bad Credentials
    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        return buildProblemDetail(ErrorType.BAD_CREDENTIALS, ex);
    }

    // 403 Account locked
    @ExceptionHandler(AccountStatusException.class)
    public ProblemDetail handleAccountStatus(AccountStatusException ex) {
        return buildProblemDetail(ErrorType.ACCOUNT_LOCKED, ex);
    }

    // 403 Access denied
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        return buildProblemDetail(ErrorType.ACCESS_DENIED, ex);
    }

    // 403 Invalid JWT signature
    @ExceptionHandler(SignatureException.class)
    public ProblemDetail handleInvalidSignature(SignatureException ex) {
        return buildProblemDetail(ErrorType.INVALID_JWT, ex);
    }

    // 403 Expired JWT token
    @ExceptionHandler(ExpiredJwtException.class)
    public ProblemDetail handleExpiredToken(ExpiredJwtException ex) {
        return buildProblemDetail(ErrorType.JWT_EXPIRED, ex);
    }

    // 500 Internal server error
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex) {
        return buildProblemDetail(ErrorType.INTERNAL_ERROR, ex);
    }

    // ✅ Utility method to create ProblemDetail and log
    private ProblemDetail buildProblemDetail(ErrorType type, Exception ex) {
        // log the stack trace
        log.error("Exception caught: ", ex);

        ProblemDetail detail = ProblemDetail.forStatusAndDetail(type.getStatus(), ex.getMessage());
        detail.setProperty("error", type.name());
        detail.setProperty("description", type.getMessage());
        detail.setProperty("timestamp", Instant.now().toString());
        return detail;
    }
}
