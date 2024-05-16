package com.zerobase.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpAccountForm {

  private String email;
  private String name; // 사용자 이름
  private String accountName; // 계좌 이름
}
