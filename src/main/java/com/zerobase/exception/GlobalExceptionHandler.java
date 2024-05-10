package com.zerobase.exception;

import com.zerobase.dto.ErrorResponse;
import java.sql.SQLIntegrityConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(AccountException.class)
  public ResponseEntity<ErrorResponse> handleAccountUserException(AccountException e) {
    log.warn(
        String.format("[%s][%s] -> %s", e.getHttpStatus(), e.getErrorCode(), e.getErrorMessage()));
    return ResponseEntity.badRequest()
        .body(new ErrorResponse(e.getErrorCode(), e.getErrorMessage()));
  }

  @ExceptionHandler(CertificationException.class)
  public ResponseEntity<ErrorResponse> handleCertificationException(CertificationException e) {
    log.warn(
        String.format("[%s][%s] -> %s", e.getHttpStatus(), e.getErrorCode(), e.getErrorMessage()));
    return ResponseEntity.internalServerError()
        .body(new ErrorResponse(e.getErrorCode(), e.getErrorMessage()));
  }

}