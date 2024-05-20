package com.zerobase.service;

import com.zerobase.domain.Transaction;
import com.zerobase.dto.TransactionDto;
import com.zerobase.exception.AccountException;
import com.zerobase.repository.AccountRepository;
import com.zerobase.repository.TransactionRepository;
import com.zerobase.type.ErrorCode;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionHistoryService {

  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;

  @Transactional
  public Page<TransactionDto> searchTransactionHistory(String accountName, LocalDate startDate,
      LocalDate endDate,
      Pageable pageable) {

    // 조회하려는 계좌를 계좌 이름(유니크)으로 찾기
    if (!accountRepository.existsByAccountName(accountName)) {
      throw new AccountException(ErrorCode.ACCOUNT_NOT_FOUND);
    }

    Page<Transaction> page
        = transactionRepository.findAllByDateBetween(startDate, endDate, pageable);

    return page.map(TransactionDto::from);
  }


}
