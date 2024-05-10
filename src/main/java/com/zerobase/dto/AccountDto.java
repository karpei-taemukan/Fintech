package com.zerobase.dto;

import com.zerobase.domain.Account;
import com.zerobase.type.AccountStatus;
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
public class AccountDto {

  private String email;
  private String name; // 계좌 소유주 이름
  private String accountName; // 계좌 이름
  private String accountNumber;
  private Long balance;


  public static AccountDto from(Account account) {
    return AccountDto.builder()
        .email(account.getAccountUser().getEmail())
        .name(account.getAccountUser().getName())
        .accountName(account.getAccountName())
        .accountNumber(account.getAccountNumber())
        .balance(account.getBalance())
        .build();
  }
}