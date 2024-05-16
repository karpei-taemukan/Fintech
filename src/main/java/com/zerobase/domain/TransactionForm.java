package com.zerobase.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionForm {

  @NotNull(message = "계좌 이름을 작성하시오")
  private String accountName;

  @NotNull(message = "금액을 작성하시오")
  @PositiveOrZero(message = "금액은 0보다 크거나 같아야 합니다")
  private Long balance;
}
