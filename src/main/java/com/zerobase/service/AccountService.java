package com.zerobase.service;

import com.zerobase.domain.Account;
import com.zerobase.domain.AccountUser;
import com.zerobase.dto.AccountDto;
import com.zerobase.exception.AccountException;
import com.zerobase.repository.AccountRepository;
import com.zerobase.repository.AccountUserRepository;
import com.zerobase.type.AccountStatus;
import com.zerobase.type.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

  private final AccountUserRepository accountUserRepository;
  private final AccountRepository accountRepository;

  @Transactional
  public List<AccountDto> showAllAccounts(String email) {

    // 가입된 사용자 계정이 있는 지 체크
    if (!accountUserRepository.existsByEmail(email)) {
      throw new AccountException(ErrorCode.USER_NOT_FOUND);
    }

    AccountUser accountUser = accountUserRepository.findByEmail(email)
        .orElseThrow(() -> new AccountException(ErrorCode.EMAIL_NOT_MATCH));


    List<Account> accounts = accountRepository.findByAccountUser(accountUser)
        .stream().filter(account -> account.getAccountStatus().equals(AccountStatus.IN_USE))
        .toList();

    return accounts.stream().map(Account::from)
        .collect(Collectors.toList());
  }

  @Transactional
  public AccountDto deleteAccount(String email, String accountName) {

    // 삭제하려는 계좌의 사용자 계정이 있는 지 체크
    if(accountUserRepository.findByEmail(email).isEmpty()){
      throw new AccountException(ErrorCode.USER_NOT_FOUND);
    }

    AccountDto accountDto = null;

    AccountUser accountUser = accountUserRepository.findByEmail(email)
        .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));

    List<Account> accounts = accountRepository.findByAccountUser(accountUser);

    for (Account account : accounts){
      if(account.getAccountName().equals(accountName)){
        // 계좌 삭제하려는 데, 계좌에 잔액이 있는 경우

        if(account.getBalance() > 0){
          throw new AccountException(ErrorCode.BALANCE_EXISTS);
        }

          account.setAccountStatus(AccountStatus.NOT_USE);
          accountRepository.save(account);
          accountDto = AccountDto.from(account);
      }
    }

    return accountDto;
  }
}