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
import java.time.LocalDate;
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
    //account.setBalance(account.getBalance() + form.getBalance());
      account.deposit(form.getBalance());

    // 거래후 금액
    Long afterTransaction = account.getBalance();

    // 거래 내역 남기기
    transactionRepository.save(
        Transaction.builder()
            .accountId(account.getId())
            .accountName(account.getAccountName())
            .beforeTransaction(beforeTransaction)
            .afterTransaction(afterTransaction)
            .date(LocalDate.now())
            .transactionType(TransactionType.DEPOSIT)
            .build());

    return AccountDto.from(account);
  }

  @Transactional
  public AccountDto withdrawAccount(String email, TransactionForm form) {
    // 이메일 체크
    if (!accountUserRepository.existsByEmail(email)) {
      throw new AccountException(ErrorCode.USER_NOT_FOUND);
    }

    Account account = accountRepository.findByAccountName(form.getAccountName())
        .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));


    // 거래전 금액
    Long beforeTransaction = account.getBalance();

    // 돈 빼기
    //account.setBalance(account.getBalance() - form.getBalance());
    account.withdraw(form.getBalance());

    // 거래후 금액
    Long afterTransaction = account.getBalance();

    // 거래 내역 남기기
    transactionRepository.save(
        Transaction.builder()
            .accountId(account.getId())
            .accountName(account.getAccountName())
            .beforeTransaction(beforeTransaction)
            .afterTransaction(afterTransaction)
            .date(LocalDate.now())
            .transactionType(TransactionType.WITHDRAW)
            .build());

    return AccountDto.from(account);
  }
}
