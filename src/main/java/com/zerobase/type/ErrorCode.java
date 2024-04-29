package com.zerobase.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  ACCOUNT_NOT_FOUND("계좌가 없습니다"),
  USER_NOT_FOUND("사용자가 없습니다"),
  LOGIN_CHECK_FAIL("로그인 실패"),
  WRONG_VERIFICATION("잘못된 인증"),
  ALREADY_VERIFICATION("이미 인증 완료"),
  EXPIRE_CODE("유효기간 지났습니다"),
  ACCOUNT_ALREADY_EXIST("이미 있는 계좌입니다"),
  EMAIL_NOT_MATCH("사용자의 이메일이 다릅니다"),
  ACCOUNT_MAX("계좌는 최대 5개");

  private final String description;
}
