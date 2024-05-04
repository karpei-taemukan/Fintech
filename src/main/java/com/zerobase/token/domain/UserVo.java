package com.zerobase.token.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserVo {
  private Long id;
  private String name;
  private String email;
  private String userType;
}
