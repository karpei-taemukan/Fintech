package com.zerobase.dto;

import com.zerobase.domain.AccountUser;
import com.zerobase.domain.SignUpUserForm;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountUserDto {

  private String email;
  private String name;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;

  public static AccountUserDto from(AccountUser accountUser) {
    return AccountUserDto.builder()
        .email(accountUser.getEmail())
        .name(accountUser.getName())
        .createdAt(accountUser.getCreatedAt())
        .modifiedAt(accountUser.getModifiedAt())
        .build();
  }
}
