package com.zerobase.exception;

import com.zerobase.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(AccountException.class)
  public ErrorResponse handleAccountUserException(AccountException e) {
    log.warn(
        String.format("[%s][%s] -> %s", e.getHttpStatus(), e.getErrorCode(), e.getErrorMessage()));
    return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
  }


  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(RandomCodeException.class)
  public ErrorResponse handleRandomCodeException(RandomCodeException e) {
    log.warn(
        String.format("[%s][%s] -> %s", e.getHttpStatus(), e.getErrorCode(), e.getErrorMessage()));
    return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
  }
}