package com.zerobase.service;

import com.zerobase.domain.Account;
import com.zerobase.domain.Transaction;
import com.zerobase.domain.TransactionForm;
import com.zerobase.dto.AccountDto;
import com.zerobase.exception.AccountException;
import com.zerobase.repository.AccountRepository;
import com.zerobase.repository.AccountUserRepository;
import com.zerobase.repository.TransactionRepository;
import com.zerobase.type.ErrorCode;
import com.zerobase.type.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {

  private final AccountUserRepository accountUserRepository;

  private final AccountRepository accountRepository;

  private final TransactionRepository transactionRepository;

  @Transactional
  public AccountDto depositAccount(String email, TransactionForm form) {
    // 이메일 체크
    if (!accountUserRepository.existsByEmail(email)) {
      throw new AccountException(ErrorCode.USER_NOT_FOUND);
    }

    Account account = accountRepository.findByAccountName(form.getAccountName())
        .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

    // 거래전 금액
    Long beforeTransaction = account.getBalance();

    // 돈 넣기
    account.setBalance(account.getBalance() + form.getBalance());

    // 거래후 금액
    Long afterTransaction = account.getBalance();

    // 거래 내역 남기기
    transactionRepository.save(
        Transaction.builder()
            .accountId(account.getId())
            .accountName(account.getAccountName())
            .beforeTransaction(beforeTransaction)
            .afterTransaction(afterTransaction)
            .transactionType(TransactionType.DEPOSIT)
            .build());

    return AccountDto.from(account);
  }


  public AccountDto withdrawAccount(String email, TransactionForm form) {
    // 이메일 체크
    if (!accountUserRepository.existsByEmail(email)) {
      throw new AccountException(ErrorCode.USER_NOT_FOUND);
    }

    Account account = accountRepository.findByAccountName(form.getAccountName())
        .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

    // 출금금액 <= 계좌 금액 이 조건 만족 해야함
    if (form.getBalance() > account.getBalance()) {
      throw new AccountException(ErrorCode.ACCOUNT_BALANCE_NOT_ENOUGH);
    }

    // 거래전 금액
    Long beforeTransaction = account.getBalance();

    // 돈 넣기
    account.setBalance(account.getBalance() - form.getBalance());

    // 거래후 금액
    Long afterTransaction = account.getBalance();

    // 거래 내역 남기기
    transactionRepository.save(
        Transaction.builder()
            .accountId(account.getId())
            .accountName(account.getAccountName())
            .beforeTransaction(beforeTransaction)
            .afterTransaction(afterTransaction)
            .transactionType(TransactionType.WITHDRAW)
            .build());

    return AccountDto.from(account);
  }
}
