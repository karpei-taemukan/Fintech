package com.zerobase.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  ACCOUNT_NOT_FOUND(HttpStatus.BAD_REQUEST,"계좌가 없습니다"),
  USER_NOT_FOUND(HttpStatus.BAD_REQUEST,"사용자가 없습니다"),
  LOGIN_CHECK_FAIL(HttpStatus.BAD_REQUEST,"로그인 실패"),
  WRONG_VERIFICATION(HttpStatus.BAD_REQUEST,"잘못된 인증"),
  ALREADY_VERIFICATION(HttpStatus.BAD_REQUEST,"이미 인증 완료"),
  EXPIRE_CODE(HttpStatus.BAD_REQUEST,"유효기간 지났습니다"),
  ACCOUNT_ALREADY_EXIST(HttpStatus.BAD_REQUEST,"이미 있는 계좌입니다"),
  EMAIL_NOT_MATCH(HttpStatus.BAD_REQUEST,"사용자의 이메일이 다릅니다"),
  ACCOUNT_MAX(HttpStatus.BAD_REQUEST,"계좌는 최대 5개"),
  ACCOUNT_NOT_MATCH(HttpStatus.BAD_REQUEST,"계좌가 일치하지않습니다"),
  DUPLICATED_RANDOM_CODE(HttpStatus.INTERNAL_SERVER_ERROR, "다시 계좌 가입 요청하세요");

  private final HttpStatus httpStatus;
  private final String description;
}