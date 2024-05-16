package com.zerobase.service;

import static org.junit.jupiter.api.Assertions.*;

import com.zerobase.config.filter.JwtFilter;
import com.zerobase.domain.Account;
import com.zerobase.domain.AccountUser;
import com.zerobase.domain.Transaction;
import com.zerobase.domain.TransactionForm;
import com.zerobase.dto.AccountDto;
import com.zerobase.exception.AccountException;
import com.zerobase.repository.AccountRepository;
import com.zerobase.repository.AccountUserRepository;
import com.zerobase.repository.TransactionRepository;
import com.zerobase.token.config.JwtAuthProvider;
import com.zerobase.type.ErrorCode;
import com.zerobase.type.TransactionType;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

  @Mock
  private JwtFilter jwtFilter;

  @Mock
  private JwtAuthProvider provider;

  @Mock
  private AccountUserRepository accountUserRepository;

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private TransactionRepository transactionRepository;

  @InjectMocks
  private TransactionService transactionService;

  @Test
  @DisplayName("입금 성공")
  void successDepositAccount() {
    String email = "abc@gmail.com";

    long beforeTransaction = 0L;
    long afterTransaction = 1000L;

    TransactionForm form = TransactionForm.builder()
        .accountName("aaa")
        .balance(1000L)
        .build();

    Account account = Account.builder()
        .id(1L)
        .accountName("aaa")
        .accountUser(AccountUser.builder()
            .name("abc")
            .email("abc@gmail.com")
            .build())
        .email(email)
        .balance(0L)
        .build();

    //given
    given(accountUserRepository.existsByEmail(anyString()))
        .willReturn(true);
    given(accountRepository.findByAccountName(anyString()))
        .willReturn(Optional.of(account));
    given(transactionRepository.save(any()))
        .willReturn(Transaction.builder()
            .accountId(account.getId())
            .accountName(account.getAccountName())
            .beforeTransaction(beforeTransaction)
            .afterTransaction(afterTransaction)
            .transactionType(TransactionType.DEPOSIT)
            .build());
    //when
    AccountDto accountDto = transactionService.depositAccount(email, form);
    //then

    assertEquals(1000L, accountDto.getBalance());
    assertEquals("aaa", accountDto.getAccountName());
  }


  @Test
  @DisplayName("입금 실패 -> 존재하지않는 계좌에 입금하는 경우")
  void failDepositAccount() {
    String email = "abc@gmail.com";
    TransactionForm form = TransactionForm.builder()
        .accountName("aaa")
        .balance(1000L)
        .build();
    //given
    given(accountUserRepository.existsByEmail(anyString()))
        .willReturn(true);
    given(accountRepository.findByAccountName(anyString()))
        .willReturn(Optional.empty());

    //when
    AccountException accountException = assertThrows(AccountException.class,
        () -> transactionService.depositAccount(email, form));
    //then
    assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, accountException.getErrorCode());
  }


  @Test
  @DisplayName("출금 성공")
  void successWithdrawAccount() {
    String email = "abc@gmail.com";

    long beforeTransaction = 1000L;
    long afterTransaction = 0L;

    TransactionForm form = TransactionForm.builder()
        .accountName("aaa")
        .balance(1000L)
        .build();

    Account account = Account.builder()
        .id(1L)
        .accountName("aaa")
        .accountUser(AccountUser.builder()
            .name("abc")
            .email("abc@gmail.com")
            .build())
        .email(email)
        .balance(1000L)
        .build();

    //given
    given(accountUserRepository.existsByEmail(anyString()))
        .willReturn(true);
    given(accountRepository.findByAccountName(anyString()))
        .willReturn(Optional.of(account));
    given(transactionRepository.save(any()))
        .willReturn(Transaction.builder()
            .accountId(account.getId())
            .accountName(account.getAccountName())
            .beforeTransaction(beforeTransaction)
            .afterTransaction(afterTransaction)
            .transactionType(TransactionType.WITHDRAW)
            .build());
    //when
    AccountDto accountDto = transactionService.withdrawAccount(email, form);
    //then

    assertEquals(0L, accountDto.getBalance());
    assertEquals("aaa", accountDto.getAccountName());

  }


  @Test
  @DisplayName("출금 실패 -> 존재하지않는 계좌에서 출금하는 경우")
  void failWithdraw_AccountNotFound() {
    String email = "abc@gmail.com";
    TransactionForm form = TransactionForm.builder()
        .accountName("aaa")
        .balance(1000L)
        .build();
    //given
    given(accountUserRepository.existsByEmail(anyString()))
        .willReturn(true);
    given(accountRepository.findByAccountName(anyString()))
        .willReturn(Optional.empty());

    //when
    AccountException accountException = assertThrows(AccountException.class,
        () -> transactionService.depositAccount(email, form));
    //then
    assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, accountException.getErrorCode());
  }


  @Test
  @DisplayName("출금 실패 -> 출금하려는 금액이 계좌 잔액보다 큰 경우")
  void failWithdraw_AccountBalanceNotEnough() {
    String email = "abc@gmail.com";

    TransactionForm form = TransactionForm.builder()
        .accountName("aaa")
        .balance(1000000L)
        .build();

    Account account = Account.builder()
        .id(1L)
        .accountName("aaa")
        .accountUser(AccountUser.builder()
            .name("abc")
            .email("abc@gmail.com")
            .build())
        .email(email)
        .balance(1000L)
        .build();

    //given
    given(accountUserRepository.existsByEmail(anyString()))
        .willReturn(true);

    given(accountRepository.findByAccountName(anyString()))
        .willReturn(Optional.of(account));

    //when
    AccountException accountException = assertThrows(AccountException.class,
        () -> transactionService.withdrawAccount(email, form));
    //then

    assertEquals(ErrorCode.ACCOUNT_BALANCE_NOT_ENOUGH, accountException.getErrorCode());

  }
}