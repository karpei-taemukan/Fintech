package com.zerobase.exception;

import com.zerobase.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificationException extends RuntimeException {
  private ErrorCode errorCode;
  private HttpStatus httpStatus;
  private String errorMessage;

  public CertificationException(ErrorCode errorCode){
    this.errorCode = errorCode;
    this.httpStatus = errorCode.getHttpStatus();
    this.errorMessage = errorCode.getDescription();
  }
}
