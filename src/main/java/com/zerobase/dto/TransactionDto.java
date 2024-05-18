package com.zerobase.dto;

import com.zerobase.domain.Account;
import com.zerobase.domain.Transaction;
import com.zerobase.type.TransactionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class TransactionDto {

  private String accountName;

  private Long beforeTransaction;

  private Long afterTransaction;

  @Enumerated(EnumType.STRING)
  private TransactionType transactionType; //  송금 / 인출 구분

  public static TransactionDto from(Transaction transaction) {
    return TransactionDto.builder()
        .accountName(transaction.getAccountName())
        .beforeTransaction(transaction.getBeforeTransaction())
        .afterTransaction(transaction.getAfterTransaction())
        .transactionType(transaction.getTransactionType())
        .build();
  }
}
