package com.zerobase.exception;

import com.zerobase.dto.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(AccountException.class)
  public ErrorResponse handleAccountUserException(AccountException e){
    return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
  }
}
